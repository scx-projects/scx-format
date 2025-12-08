package dev.scx.format.xml;

import com.ctc.wstx.stax.WstxInputFactory;
import com.ctc.wstx.stax.WstxOutputFactory;
import dev.scx.format.FormatNodeConverter;
import dev.scx.format.FormatToNodeException;
import dev.scx.format.NodeToFormatException;
import dev.scx.node.Node;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.nio.charset.Charset;

import static com.ctc.wstx.api.WstxInputProperties.*;
import static dev.scx.format.xml.AutoCloseableXMLStreamReader.wrapReader;
import static dev.scx.format.xml.AutoCloseableXMLStreamWriter.wrapWriter;
import static dev.scx.format.xml.XmlDeserializer.deserialize;

/// XmlNodeConverter
///
/// @author scx567888
/// @version 0.0.1
public final class XmlNodeConverter implements FormatNodeConverter<XmlNodeConverterOptions> {

    public XmlNodeConverter() {

    }

    @Override
    public Node formatToNode(Reader reader, XmlNodeConverterOptions options) throws FormatToNodeException, IOException {
        try (var xmlStreamReader = createXMLStreamReader(reader, options)) {
            return deserialize(xmlStreamReader.reader());
        } catch (XMLStreamException e) {
            throw new FormatToNodeException(e);
        }
    }

    @Override
    public Node formatToNode(InputStream inputStream, Charset charset, XmlNodeConverterOptions options) throws FormatToNodeException, IOException {
        try (var xmlStreamReader = createXMLStreamReader(inputStream, charset.name(), options)) {
            return deserialize(xmlStreamReader.reader());
        } catch (XMLStreamException e) {
            throw new FormatToNodeException(e);
        }
    }

    @Override
    public Node formatToNode(String text, XmlNodeConverterOptions options) throws FormatToNodeException {
        try (var reader = new StringReader(text)) {
            return formatToNode(reader, options);
        } catch (IOException e) {
            throw new FormatToNodeException(e);
        }
    }

    @Override
    public Node formatToNode(byte[] bytes, Charset charset, XmlNodeConverterOptions options) throws FormatToNodeException {
        try (var inputStream = new ByteArrayInputStream(bytes)) {
            return formatToNode(inputStream, charset, options);
        } catch (IOException e) {
            throw new FormatToNodeException(e);
        }
    }

    @Override
    public Node formatToNode(File file, Charset charset, XmlNodeConverterOptions options) throws FormatToNodeException, IOException {
        try (var xmlStreamReader = createXMLStreamReader(file, options)) {
            return deserialize(xmlStreamReader.reader());
        } catch (XMLStreamException e) {
            throw new FormatToNodeException(e);
        }
    }

    @Override
    public void nodeToFormat(Node node, Writer writer, XmlNodeConverterOptions options) throws NodeToFormatException, IOException {
        try (var xmlStreamWriter = createXMLStreamWriter(writer, options)) {
            new XmlSerializer(options).serialize(xmlStreamWriter.writer(), node);
        } catch (XMLStreamException e) {
            throw new NodeToFormatException(e);
        }
    }

    @Override
    public void nodeToFormat(Node node, OutputStream outputStream, Charset charset, XmlNodeConverterOptions options) throws NodeToFormatException, IOException {
        try (var xmlStreamWriter = createXMLStreamWriter(outputStream, charset.name(), options)) {
            new XmlSerializer(options).serialize(xmlStreamWriter.writer(), node);
        } catch (XMLStreamException e) {
            throw new NodeToFormatException(e);
        }
    }

    @Override
    public String nodeToFormatString(Node node, XmlNodeConverterOptions options) throws NodeToFormatException {
        try (var writer = new StringWriter()) {
            nodeToFormat(node, writer, options);
            return writer.toString();
        } catch (IOException e) {
            throw new NodeToFormatException(e);
        }
    }

    @Override
    public byte[] nodeToFormatBytes(Node node, Charset charset, XmlNodeConverterOptions options) throws NodeToFormatException {
        try (var outputStream = new ByteArrayOutputStream()) {
            nodeToFormat(node, outputStream, charset, options);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new NodeToFormatException(e);
        }
    }

    @Override
    public File nodeToFormatFile(Node node, File file, Charset charset, XmlNodeConverterOptions options) throws NodeToFormatException, IOException {
        try (var outputStream = new FileOutputStream(file)) {
            nodeToFormat(node, outputStream, charset, options);
            return file;
        }
    }

    private WstxInputFactory createWstxInputFactory(XmlNodeConverterOptions options) {
        // 这里我们使用 WstxInputFactory, 因为默认 XMLInputFactory 功能过于羸弱
        var xmlInputFactory = new WstxInputFactory();
        //有很多的 安全限制 Woodstox  已经覆盖了 我们直接使用
        xmlInputFactory.setProperty(P_MAX_ELEMENT_DEPTH, options.maxNestingDepth());
        xmlInputFactory.setProperty(P_MAX_TEXT_LENGTH, options.maxStringLength());
        xmlInputFactory.setProperty(P_MAX_ATTRIBUTE_SIZE, options.maxStringLength());
        xmlInputFactory.setProperty(P_MAX_CHILDREN_PER_ELEMENT, options.maxChildCount());
        xmlInputFactory.setProperty(P_MAX_ATTRIBUTES_PER_ELEMENT, options.maxChildCount());
        xmlInputFactory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        xmlInputFactory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
        return xmlInputFactory;
    }

    private AutoCloseableXMLStreamReader createXMLStreamReader(Reader reader, XmlNodeConverterOptions options) throws XMLStreamException {
        var xmlInputFactory = createWstxInputFactory(options);
        return wrapReader(xmlInputFactory.createXMLStreamReader(reader));
    }

    private AutoCloseableXMLStreamReader createXMLStreamReader(InputStream inputStream, String enc, XmlNodeConverterOptions options) throws XMLStreamException {
        var xmlInputFactory = createWstxInputFactory(options);
        return wrapReader(xmlInputFactory.createXMLStreamReader(inputStream, enc));
    }

    private AutoCloseableXMLStreamReader createXMLStreamReader(File file, XmlNodeConverterOptions options) throws XMLStreamException {
        var xmlInputFactory = createWstxInputFactory(options);
        return wrapReader(xmlInputFactory.createXMLStreamReader(file));
    }

    private AutoCloseableXMLStreamWriter createXMLStreamWriter(Writer writer, XmlNodeConverterOptions options) throws XMLStreamException {
        var xmlOutputFactory = new WstxOutputFactory();
        return wrapWriter(xmlOutputFactory.createXMLStreamWriter(writer));
    }

    private AutoCloseableXMLStreamWriter createXMLStreamWriter(OutputStream outputStream, String enc, XmlNodeConverterOptions options) throws XMLStreamException {
        var xmlOutputFactory = new WstxOutputFactory();
        return wrapWriter(xmlOutputFactory.createXMLStreamWriter(outputStream, enc));
    }

}
