package dev.scx.format.json;

import dev.scx.format.FormatNodeConverter;
import dev.scx.format.FormatToNodeException;
import dev.scx.format.NodeToFormatException;
import dev.scx.node.Node;
import tools.jackson.core.*;
import tools.jackson.core.sym.ByteQuadsCanonicalizer;
import tools.jackson.core.util.BufferRecycler;
import tools.jackson.core.util.JsonRecyclerPools;
import tools.jackson.core.util.RecyclerPool;

import java.io.*;
import java.nio.charset.Charset;

import static dev.scx.format.json.JsonSerializer.serialize;
import static dev.scx.format.json.PrettyPrintObjectWriteContext.PRETTY_PRINT_OBJECT_WRITE_CONTEXT;

/// JsonNodeConverter
///
/// @author scx567888
/// @version 0.0.1
public final class JsonNodeConverter implements FormatNodeConverter<JsonNodeConvertOptions> {

    private final ByteQuadsCanonicalizer _byteSymbolCanonicalizer;
    private final RecyclerPool<BufferRecycler> _recyclerPool;

    public JsonNodeConverter() {
        // 这两个对象的创建很耗性能 我们在此复用
        this._byteSymbolCanonicalizer = ByteQuadsCanonicalizer.createRoot();
        this._recyclerPool = JsonRecyclerPools.defaultPool();
    }

    @Override
    public Node formatToNode(Reader reader, JsonNodeConvertOptions options) throws FormatToNodeException, IOException {
        var jsonFactory = createJsonFactory(options);
        var readContext = createReadContext(options);
        try (var parser = jsonFactory.createParser(readContext, reader)) {
            return new JsonDeserializer(options).deserialize(parser);
        } catch (JacksonException e) {
            throw new FormatToNodeException(e);
        }
    }

    @Override
    public Node formatToNode(InputStream inputStream, Charset charset, JsonNodeConvertOptions options) throws FormatToNodeException, IOException {
        var jsonFactory = createJsonFactory(options);
        var readContext = createReadContext(options);
        try (var parser = jsonFactory.createParser(readContext, inputStream)) {
            return new JsonDeserializer(options).deserialize(parser);
        } catch (JacksonException e) {
            throw new FormatToNodeException(e);
        }
    }

    @Override
    public Node formatToNode(String string, JsonNodeConvertOptions options) throws FormatToNodeException {
        var jsonFactory = createJsonFactory(options);
        var readContext = createReadContext(options);
        try (var parser = jsonFactory.createParser(readContext, string)) {
            return new JsonDeserializer(options).deserialize(parser);
        } catch (JacksonException e) {
            throw new FormatToNodeException(e);
        }
    }

    @Override
    public Node formatToNode(byte[] bytes, Charset charset, JsonNodeConvertOptions options) throws FormatToNodeException {
        var jsonFactory = createJsonFactory(options);
        var readContext = createReadContext(options);
        try (var parser = jsonFactory.createParser(readContext, bytes)) {
            return new JsonDeserializer(options).deserialize(parser);
        } catch (JacksonException e) {
            throw new FormatToNodeException(e);
        }
    }

    @Override
    public Node formatToNode(File file, Charset charset, JsonNodeConvertOptions options) throws FormatToNodeException, IOException {
        var jsonFactory = createJsonFactory(options);
        var readContext = createReadContext(options);
        try (var parser = jsonFactory.createParser(readContext, file)) {
            return new JsonDeserializer(options).deserialize(parser);
        } catch (JacksonException e) {
            throw new FormatToNodeException(e);
        }
    }

    @Override
    public void nodeToFormat(Node node, Writer writer, JsonNodeConvertOptions options) throws NodeToFormatException, IOException {
        var jsonFactory = createJsonFactory(options);
        var writeContext = createWriteContext(options);
        try (var generator = jsonFactory.createGenerator(writeContext, writer)) {
            serialize(generator, node);
        } catch (JacksonException e) {
            throw new NodeToFormatException(e);
        }
    }

    @Override
    public void nodeToFormat(Node node, OutputStream outputStream, Charset charset, JsonNodeConvertOptions options) throws NodeToFormatException, IOException {
        var jsonFactory = createJsonFactory(options);
        var writeContext = createWriteContext(options);
        try (var generator = jsonFactory.createGenerator(writeContext, outputStream)) {
            serialize(generator, node);
        } catch (JacksonException e) {
            throw new NodeToFormatException(e);
        }
    }

    @Override
    public String nodeToFormatString(Node node, JsonNodeConvertOptions options) throws NodeToFormatException {
        try (var writer = new StringWriter()) {
            nodeToFormat(node, writer, options);
            return writer.toString();
        } catch (IOException e) {
            throw new NodeToFormatException(e);
        }
    }

    @Override
    public byte[] nodeToFormatBytes(Node node, Charset charset, JsonNodeConvertOptions options) throws NodeToFormatException {
        try (var outputStream = new ByteArrayOutputStream()) {
            nodeToFormat(node, outputStream, charset, options);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new NodeToFormatException(e);
        }
    }

    @Override
    public File nodeToFormatFile(Node node, File file, Charset charset, JsonNodeConvertOptions options) throws NodeToFormatException, IOException {
        var jsonFactory = createJsonFactory(options);
        var writeContext = createWriteContext(options);
        try (var generator = jsonFactory.createGenerator(writeContext, file, JsonEncoding.UTF8)) {
            serialize(generator, node);
            return file;
        } catch (JacksonException e) {
            throw new NodeToFormatException(e);
        }
    }

    private TokenStreamFactory createJsonFactory(JsonNodeConvertOptions options) {
        return new LightJsonFactoryBuilder(options, _byteSymbolCanonicalizer, _recyclerPool).build();
    }

    private ObjectReadContext createReadContext(JsonNodeConvertOptions options) {
        return ObjectReadContext.empty();
    }

    private ObjectWriteContext createWriteContext(JsonNodeConvertOptions options) {
        if (options.prettyPrint()) {
            return PRETTY_PRINT_OBJECT_WRITE_CONTEXT;
        }
        return ObjectWriteContext.empty();
    }

}
