package dev.scx.format.xml;

import dev.scx.node.*;
import org.codehaus.stax2.XMLStreamReader2;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;

/// ### 解析规则:
///
/// 1. `<a></a>` -> `""`
///     没有文本和子元素 -> StringNode("")
///
/// 2. `<a/>` -> `NULL`
///     没有文本和子元素 (自闭合标签) -> NULL
///
/// 3. `<a>123</a>` -> `"123"`
///     只有文本时 -> StringNode
///
/// 4. `<a><b>123</b></a>` -> `{"b": "123"}`
///     存在子元素 -> ObjectNode
///
/// 5. `<a name="jack"></a>` 或 `<a name="jack" />` -> `{"name": "jack"}`
///     存在属性 (无论是否自闭合) -> ObjectNode
///
/// 6. `<a name="jack"><age>18</age></a>` -> `{"name": "jack", "age": "18"}`
///     属性和子元素相同方式看待 -> ObjectNode
///
/// 7. `<a>000<b>123</b></a>` -> `{"b": "123", "": "000"}`
///     同时存在子元素和单个文本 -> 将文本视为单个 StringNode, 并以 "" 为 key 合并到 ObjectNode 中
///
/// 8. `<a>000<b>123</b>6666</a>` -> `{"b": "123", "": ["000", "6666"]}`
///     同时存在子元素和多个文本 -> 将多个文本视为 ArrayNode(StringNode[]) , 并以 "" 为 key 合并到 Object 中
///
/// 9. `<a><b>123</b><b>456</b></a>` -> `{"b": ["123", "456"]}`
///     存在多个同名子元素 -> 合并子元素为 ArrayNode
///
/// 10, `<a name="jack" name="rose"></a>` -> `{"name": ["jack", "rose"]}`
///     存在多个同名属性 -> 合并属性为 ArrayNode
///
/// 11, `<a name="">  <b> 1 2 3 </b>   </a>` -> `{"b": " 1 2 3 ", "name": "" }`
///     所有的纯空白文本节点视为不存在, 但有内容则保留原始文本, 属性永远保留原始文本
///
/// @author scx567888
/// @version 0.0.1
final class XmlDeserializer {

    public static Node deserialize(XMLStreamReader2 reader) throws XMLStreamException {
        // 1, 循环直到找到第一个元素起始
        while (true) {
            int eventType = reader.getEventType();
            if (eventType == XMLStreamConstants.START_ELEMENT) {
                break;
            }
            reader.next();
        }
        // 2, 解析
        var node = _deserializeElement(reader);

        // 3, 验证是否存在后续多余内容
        while (reader.hasNext()) {
            // 非法内容 Woodstox 会为直接抛异常 无需我们处理
            reader.next();
        }

        return node;
    }

    private static Node _deserializeElement(XMLStreamReader2 reader) throws XMLStreamException {
        // 记录出现过的子元素和属性
        var elements = new ObjectNode();

        // 1, 处理当前元素的属性
        int attributeCount = reader.getAttributeCount();
        for (int i = 0; i < attributeCount; i = i + 1) {
            var name = reader.getAttributeLocalName(i);
            var value = reader.getAttributeValue(i);
            // 可能存在重名元素
            var oldChildNode = elements.get(name);
            if (oldChildNode == null) {
                elements.put(name, new StringNode(value));
                continue;
            }
            //我们默认尝试转换成 数组
            if (oldChildNode instanceof ArrayNode arrayNode) {
                arrayNode.add(new StringNode(value));
            } else {
                var arrayNode = new ArrayNode();
                arrayNode.add(oldChildNode);
                arrayNode.add(new StringNode(value));
                elements.put(name, arrayNode);
            }
        }

        // 2, 判断是否是自闭合标签
        var emptyElement = reader.isEmptyElement();
        // 自闭合标签 无需处理内部元素 直接返回
        if (emptyElement) {
            // 这里别忘了移动
            reader.next();
            if (elements.isEmpty()) {
                return NullNode.NULL;
            } else {
                return elements;
            }
        }

        // 记录出现过的文本
        var texts = new ArrayNode();

        while (true) {
            var eventType = reader.next();
            // 如果又遇到了一个 ELEMENT 进行递归解析
            if (eventType == XMLStreamConstants.START_ELEMENT) {
                var name = reader.getLocalName();
                var element = _deserializeElement(reader);
                // 可能存在重名元素
                var oldChildNode = elements.get(name);
                if (oldChildNode == null) {
                    elements.put(name, element);
                    continue;
                }
                //我们默认尝试转换成 数组
                if (oldChildNode instanceof ArrayNode arrayNode) {
                    arrayNode.add(element);
                } else {
                    var arrayNode = new ArrayNode();
                    arrayNode.add(oldChildNode);
                    arrayNode.add(element);
                    elements.put(name, arrayNode);
                }
            } else if (eventType == XMLStreamConstants.CHARACTERS) {
                // 遇到了文本 进行存储
                var text = reader.getText();
                // 忽略空白字符
                if (!text.isBlank()) {
                    texts.add(new StringNode(text));
                }
            } else if (eventType == XMLStreamConstants.END_ELEMENT) {
                // 跳出循环
                break;
            }
            // 其余的 我们全部 当做不存在, 比如注释之类
        }

        // 没有任何子元素
        if (elements.isEmpty()) {
            // 如果文本也是空的
            if (texts.isEmpty()) {
                return new StringNode("");
            }
            // 如果只有一个文本节点
            if (texts.size() == 1) {
                return texts.get(0);
            }
            // 有很多文本节点 (应该不会出现这种情况)
            return texts;
        }
        // 如果只有一个文本节点
        if (texts.size() == 1) {
            elements.put("", texts.get(0));
            return elements;
        } else if (texts.size() > 1) {
            // 如果又很多文本节点 以数组形式添加
            elements.put("", texts);
        }

        return elements;
    }

}
