package dev.scx.format.json;

import dev.scx.node.*;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;

/// ### 关于序列化
/// 此序列化器基于递归下降方式进行序列化, 以保证代码的简洁和可维护性.
/// 但 Node 实际上允许自引用, 也就是说存在无限递归导致栈溢出的风险.
/// 因此, 我们通过 [JsonNodeConvertOptions#maxNestingDepth(int)] 来间接限制递归深度,
/// 避免超过 JVM 栈限制 (一般超过 3500 层为危险值)
///
/// @author scx567888
/// @version 0.0.1
final class JsonSerializer {

    public static void serialize(JsonGenerator g, Node node) throws JacksonException {
        switch (node) {
            case ObjectNode objectNode -> {
                g.writeStartObject(objectNode, objectNode.size());
                for (var field : objectNode) {
                    g.writeName(field.getKey());
                    serialize(g, field.getValue());
                }
                g.writeEndObject();
            }
            case ArrayNode arrayNode -> {
                g.writeStartArray(arrayNode, arrayNode.size());
                for (var element : arrayNode) {
                    serialize(g, element);
                }
                g.writeEndArray();
            }
            case StringNode stringNode -> g.writeString(stringNode.value());
            case IntNode intNode -> g.writeNumber(intNode.value());
            case LongNode longNode -> g.writeNumber(longNode.value());
            case FloatNode floatNode -> g.writeNumber(floatNode.value());
            case DoubleNode doubleNode -> g.writeNumber(doubleNode.value());
            case BigIntegerNode bigIntegerNode -> g.writeNumber(bigIntegerNode.value());
            case BigDecimalNode bigDecimalNode -> g.writeNumber(bigDecimalNode.value());
            case BooleanNode booleanNode -> g.writeBoolean(booleanNode.value());
            case NullNode _ -> g.writeNull();
        }
    }

}
