package dev.scx.format.xml;

import org.codehaus.stax2.XMLStreamReader2;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/// AutoCloseableXMLStreamReader
///
/// @author scx567888
/// @version 0.0.1
record AutoCloseableXMLStreamReader(XMLStreamReader2 reader) implements AutoCloseable {

    public static AutoCloseableXMLStreamReader wrapReader(XMLStreamReader reader) {
        // 这里我们保证 XMLStreamReader 一定是 XMLStreamReader2
        return new AutoCloseableXMLStreamReader((XMLStreamReader2) reader);
    }

    @Override
    public void close() throws XMLStreamException {
        reader.closeCompletely();
    }

}
