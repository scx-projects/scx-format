package dev.scx.format.xml;

import dev.scx.format.NodeToFormatException;
import dev.scx.node.*;
import org.codehaus.stax2.XMLStreamWriter2;

import javax.xml.stream.XMLStreamException;

/// ### 序列化规则
///
/// 0. 跟标签默认 -> root, 没有上下文 key 可用的数组 默认 -> item.
///    支持外部配置, 防止某些情况下的冲突
///
/// 1. `"123"` -> `<root>123</root>`
///    值类型 -> 标准标签
///
/// 2. `NULL` -> `<root/>`
///    NULL -> 闭合标签
///
/// 3. `{"a": 123}` -> `<root><a>123</a></root>`
///    对象类型 -> 嵌套标签
///
/// 4. `{"a": [1, 2]}` -> `<root><a>1</a><a>2</a></root>``
///    数组 -> 尝试重复
///
/// 5. `[1, 2]` -> `<root><item>1</item><item>2</item></root>`
///    数组没有可用的上文 key -> 使用 item
///
/// 6. `[1, [2]]` -> `<root><item>1</item><item><item>2</item></item></root>`
///    嵌套数组不进行任何扁平化
///
/// 7, `{"": 123}` -> `<root>123</root>`
///    key 为 "", 直接解包
///
/// @author scx567888
/// @version 0.0.1
final class XmlSerializer {

    private final String rootName;
    private final int maxNestingDepth;
    private final String itemName;

    public XmlSerializer(XmlNodeConverterOptions options) {
        this.rootName = options.rootName();
        this.maxNestingDepth = options.maxNestingDepth();
        this.itemName = options.itemName();
    }

    public void serialize(XMLStreamWriter2 writer2, Node node) throws XMLStreamException, NodeToFormatException {
        // 顶级数组需要特殊处理
        var isRootArray = node instanceof ArrayNode;
        _serialize(writer2, node, rootName, isRootArray, 1);
    }

    private void _serialize(XMLStreamWriter2 writer2, Node node, String key, boolean inArray, int currentDepth) throws XMLStreamException, NodeToFormatException {
        if (currentDepth > maxNestingDepth) {
            throw new NodeToFormatException("Nesting depth exceeds limit: " + maxNestingDepth);
        }
        switch (node) {
            case NullNode _ -> {
                // 如果根节点本身就是 null, 直接返回自闭合标签
                writer2.writeEmptyElement(key);
            }
            case ValueNode valueNode -> {
                // "", 直接解包
                if (key.isEmpty()) {
                    writer2.writeCharacters(valueNode.toString());
                } else {
                    writer2.writeStartElement(key);
                    writer2.writeCharacters(valueNode.toString());
                    writer2.writeEndElement();
                }
            }
            case ObjectNode objectNode -> {
                writer2.writeStartElement(key);
                for (var e : objectNode) {
                    _serialize(writer2, e.getValue(), e.getKey(), false, currentDepth + 1);
                }
                writer2.writeEndElement();
            }
            case ArrayNode arrayNode -> {
                if (inArray) {
                    writer2.writeStartElement(key);
                    for (var e : arrayNode) {
                        _serialize(writer2, e, itemName, true, currentDepth + 1);
                    }
                    writer2.writeEndElement();
                } else {
                    for (var e : arrayNode) {
                        _serialize(writer2, e, key, true, currentDepth + 1);
                    }
                }
            }
        }
    }

}
