package dev.scx.format.json;

import tools.jackson.core.*;
import tools.jackson.core.base.TextualTSFactory;
import tools.jackson.core.io.CharacterEscapes;
import tools.jackson.core.io.IOContext;
import tools.jackson.core.json.*;
import tools.jackson.core.sym.ByteQuadsCanonicalizer;
import tools.jackson.core.sym.CharsToNameCanonicalizer;

import java.io.*;

/// 轻量级的 JsonFactory 允许频繁创建.
///
/// @author scx567888
/// @version 0.0.1
/// @see JsonFactory
final class LightJsonFactory extends TextualTSFactory {

    private final static String FORMAT_NAME_JSON = "JSON";

    private final CharacterEscapes _characterEscapes;
    private final SerializableString _rootValueSeparator;
    private final int _maximumNonEscapedChar;
    private final char _quoteChar;
    private final CharsToNameCanonicalizer _rootCharSymbols;
    private final ByteQuadsCanonicalizer _byteSymbolCanonicalizer;

    public LightJsonFactory(LightJsonFactoryBuilder b) {
        super(b);
        this._rootValueSeparator = b._jacksonConfig.rootValueSeparator();
        this._characterEscapes = b._jacksonConfig.characterEscapes();
        this._maximumNonEscapedChar = b._jacksonConfig.highestNonEscapedChar();
        this._quoteChar = b._jacksonConfig.quoteChar();
        this._rootCharSymbols = CharsToNameCanonicalizer.createRoot(this);
        this._byteSymbolCanonicalizer = b._byteSymbolCanonicalizer;
    }

    @Override
    public LightJsonFactoryBuilder rebuild() {
        throw new UnsupportedOperationException();
    }

    @Override
    public LightJsonFactory copy() {
        return this;
    }

    @Override
    public TokenStreamFactory snapshot() {
        return this;
    }

    @Override
    public Version version() {
        return PackageVersion.VERSION;
    }

    @Override
    public boolean canParseAsync() {
        // Jackson 2.9 and later do support async parsing for JSON
        return true;
    }

    @Override
    public boolean canUseSchema(FormatSchema schema) {
        return false; // no FormatSchema for json
    }

    @Override
    public String getFormatName() {
        return FORMAT_NAME_JSON;
    }

    @Override
    protected JsonParser _createParser(ObjectReadContext readCtxt, IOContext ioCtxt,
                                       InputStream in) throws JacksonException {
        try {
            return new ByteSourceJsonBootstrapper(ioCtxt, in)
                .constructParser(readCtxt,
                    readCtxt.getStreamReadFeatures(_streamReadFeatures),
                    readCtxt.getFormatReadFeatures(_formatReadFeatures),
                    _byteSymbolCanonicalizer, _rootCharSymbols, _factoryFeatures);
        } catch (RuntimeException e) {
            // 10-Jun-2022, tatu: For [core#763] may need to close InputStream here
            if (ioCtxt.isResourceManaged()) {
                try {
                    in.close();
                } catch (Exception e2) {
                    e.addSuppressed(e2);
                }
            }
            throw e;
        }
    }

    @Override
    protected JsonParser _createParser(ObjectReadContext readCtxt, IOContext ioCtxt,
                                       Reader r) throws JacksonException {
        return new ReaderBasedJsonParser(readCtxt, ioCtxt,
            readCtxt.getStreamReadFeatures(_streamReadFeatures),
            readCtxt.getFormatReadFeatures(_formatReadFeatures),
            r,
            _rootCharSymbols.makeChild());
    }

    @Override
    protected JsonParser _createParser(ObjectReadContext readCtxt, IOContext ioCtxt,
                                       char[] data, int offset, int len,
                                       boolean recyclable) throws JacksonException {
        _checkRangeBoundsForCharArray(data, offset, len);
        return new ReaderBasedJsonParser(readCtxt, ioCtxt,
            readCtxt.getStreamReadFeatures(_streamReadFeatures),
            readCtxt.getFormatReadFeatures(_formatReadFeatures),
            null,
            _rootCharSymbols.makeChild(),
            data, offset, offset + len, recyclable);
    }

    @Override
    protected JsonParser _createParser(ObjectReadContext readCtxt, IOContext ioCtxt,
                                       byte[] data, int offset, int len)
        throws JacksonException {
        _checkRangeBoundsForByteArray(data, offset, len);
        return new ByteSourceJsonBootstrapper(ioCtxt, data, offset, len)
            .constructParser(readCtxt,
                readCtxt.getStreamReadFeatures(_streamReadFeatures),
                readCtxt.getFormatReadFeatures(_formatReadFeatures),
                _byteSymbolCanonicalizer, _rootCharSymbols, _factoryFeatures);
    }

    @Override
    protected JsonParser _createParser(ObjectReadContext readCtxt, IOContext ioCtxt,
                                       DataInput input)
        throws JacksonException {
        // Also: while we can't do full bootstrapping (due to read-ahead limitations), should
        // at least handle possible UTF-8 BOM
        int firstByte = ByteSourceJsonBootstrapper.skipUTF8BOM(input);
        ByteQuadsCanonicalizer can = _byteSymbolCanonicalizer.makeChildOrPlaceholder(_factoryFeatures);
        return new UTF8DataInputJsonParser(readCtxt, ioCtxt,
            readCtxt.getStreamReadFeatures(_streamReadFeatures),
            readCtxt.getFormatReadFeatures(_formatReadFeatures),
            input, can, firstByte);
    }

    @Override
    protected JsonGenerator _createGenerator(ObjectWriteContext writeCtxt,
                                             IOContext ioCtxt, Writer out)
        throws JacksonException {
        SerializableString rootSep = writeCtxt.getRootValueSeparator(_rootValueSeparator);
        // May get Character-Escape overrides from context; if not, use factory's own
        // (which default to `null`)
        CharacterEscapes charEsc = writeCtxt.getCharacterEscapes();
        if (charEsc == null) {
            charEsc = _characterEscapes;
        }
        // 14-Jan-2019, tatu: Should we make this configurable via databind layer?
        final int maxNonEscaped = _maximumNonEscapedChar;
        // NOTE: JSON generator does not use schema
        return new WriterBasedJsonGenerator(writeCtxt, ioCtxt,
            writeCtxt.getStreamWriteFeatures(_streamWriteFeatures),
            writeCtxt.getFormatWriteFeatures(_formatWriteFeatures),
            out,
            rootSep, writeCtxt.getPrettyPrinter(), charEsc, maxNonEscaped, _quoteChar);
    }

    @Override
    protected JsonGenerator _createUTF8Generator(ObjectWriteContext writeCtxt,
                                                 IOContext ioCtxt, OutputStream out) throws JacksonException {
        SerializableString rootSep = writeCtxt.getRootValueSeparator(_rootValueSeparator);
        // May get Character-Escape overrides from context; if not, use factory's own
        // (which default to `null`)
        CharacterEscapes charEsc = writeCtxt.getCharacterEscapes();
        if (charEsc == null) {
            charEsc = _characterEscapes;
        }
        // 14-Jan-2019, tatu: Should we make this configurable via databind layer?
        final int maxNonEscaped = _maximumNonEscapedChar;
        // NOTE: JSON generator does not use schema

        return new UTF8JsonGenerator(writeCtxt, ioCtxt,
            writeCtxt.getStreamWriteFeatures(_streamWriteFeatures),
            writeCtxt.getFormatWriteFeatures(_formatWriteFeatures),
            out,
            rootSep, charEsc, writeCtxt.getPrettyPrinter(), maxNonEscaped, _quoteChar);
    }

}
