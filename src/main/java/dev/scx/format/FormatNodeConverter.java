package dev.scx.format;

import dev.scx.node.Node;

import java.io.*;
import java.nio.charset.Charset;

/// 格式 <-> Node 转换器
///
/// 不会自动关闭传入的资源
///
/// @author scx567888
/// @version 0.0.1
public interface FormatNodeConverter<O extends FormatNodeConvertOptions> {

    Node formatToNode(Reader reader, O options) throws FormatToNodeException, IOException;

    Node formatToNode(InputStream inputStream, Charset charset, O options) throws FormatToNodeException, IOException;

    Node formatToNode(String text, O options) throws FormatToNodeException;

    Node formatToNode(byte[] bytes, Charset charset, O options) throws FormatToNodeException;

    Node formatToNode(File file, Charset charset, O options) throws FormatToNodeException, IOException;

    void nodeToFormat(Node node, Writer writer, O options) throws NodeToFormatException, IOException;

    void nodeToFormat(Node node, OutputStream outputStream, Charset charset, O options) throws NodeToFormatException, IOException;

    String nodeToFormatString(Node node, O options) throws NodeToFormatException;

    byte[] nodeToFormatBytes(Node node, Charset charset, O options) throws NodeToFormatException;

    File nodeToFormatFile(Node node, File file, Charset charset, O options) throws NodeToFormatException, IOException;

}
