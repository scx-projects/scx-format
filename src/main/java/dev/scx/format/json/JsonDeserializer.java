package dev.scx.format.json;

import dev.scx.node.*;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.exc.StreamReadException;

import static dev.scx.node.BooleanNode.FALSE;
import static dev.scx.node.BooleanNode.TRUE;
import static dev.scx.node.NullNode.NULL;

/// JsonDeserializer
///
/// @author scx567888
/// @version 0.0.1
final class JsonDeserializer {

    private final DuplicateFieldPolicy _duplicateFieldPolicy;

    public JsonDeserializer(JsonNodeConvertOptions options) {
        this._duplicateFieldPolicy = options.duplicateFieldPolicy();
    }

    public Node deserialize(JsonParser p) throws JacksonException {
        var firstToken = p.nextToken();
        if (firstToken == null) {
            throw new StreamReadException(p, "未检测到任何有效内容");
        }

        var resultNode = _deserialize(p);

        var tailToken = p.nextToken();
        if (tailToken != null) {
            throw new StreamReadException(p, "检测到多余内容");
        }
        return resultNode;
    }

    private Node _deserialize(JsonParser p) {
        var stack = new ContainerStack();
        var currentToken = p.currentToken();
        return switch (currentToken) {
            case START_OBJECT -> _deserializeContainerNoRecursion(p, stack, new ObjectNode());
            case START_ARRAY -> _deserializeContainerNoRecursion(p, stack, new ArrayNode());
            default -> _deserializeAnyScalar(p);
        };
    }

    // Non-recursive alternative
    private ContainerNode _deserializeContainerNoRecursion(JsonParser p, ContainerStack stack, final ContainerNode root) throws JacksonException {
        ContainerNode curr = root;

        outer_loop:
        do {
            switch (curr) {
                case ObjectNode currObject -> {

                    objectLoop:
                    while (true) {
                        var propName = p.nextName();
                        if (propName == null) {
                            break objectLoop;
                        }
                        Node value;
                        JsonToken t = p.nextToken();
                        if (t == null) { // unexpected end-of-input (or bad buffering?)
                            t = JsonToken.NOT_AVAILABLE; // to trigger an exception
                        }
                        switch (t) {
                            case START_OBJECT -> {
                                ObjectNode newOb = new ObjectNode();
                                Node old = currObject.put(propName, newOb);
                                if (old != null) {
                                    _handleDuplicateProperty(p, propName, currObject, old, newOb);
                                }
                                stack.push(curr);
                                curr = newOb;
                                continue outer_loop;
                            }
                            case START_ARRAY -> {
                                ArrayNode newOb = new ArrayNode();
                                Node old = currObject.put(propName, newOb);
                                if (old != null) {
                                    _handleDuplicateProperty(p, propName, currObject, old, newOb);
                                }
                                stack.push(curr);
                                curr = newOb;
                                continue outer_loop;
                            }
                            case END_OBJECT -> {
                                break objectLoop;
                            }
                            case END_ARRAY -> {
                                throw new StreamReadException(p, "Unexpected token: " + t);
                            }
                            case VALUE_STRING -> value = new StringNode(p.getString());
                            case VALUE_NUMBER_INT -> value = _fromInt(p);
                            case VALUE_NUMBER_FLOAT -> value = _fromFloat(p);
                            case VALUE_TRUE -> value = TRUE;
                            case VALUE_FALSE -> value = FALSE;
                            case VALUE_NULL -> value = NULL;
                            default -> throw new StreamReadException(p, "Unexpected token: " + t);
                        }
                        Node old = currObject.put(propName, value);
                        if (old != null) {
                            _handleDuplicateProperty(p, propName, currObject, old, value);
                        }
                    }
                    // reached not-property-name, should be END_OBJECT (verify?)
                }
                case ArrayNode currArray -> {

                    arrayLoop:
                    while (true) {
                        Node value;
                        JsonToken t = p.nextToken();
                        if (t == null) { // unexpected end-of-input (or bad buffering?)
                            t = JsonToken.NOT_AVAILABLE; // to trigger an exception
                        }
                        switch (t) {
                            case START_OBJECT -> {
                                stack.push(curr);
                                curr = new ObjectNode();
                                currArray.add(curr);
                                continue outer_loop;
                            }
                            case START_ARRAY -> {
                                stack.push(curr);
                                curr = new ArrayNode();
                                currArray.add(curr);
                                continue outer_loop;
                            }
                            case END_OBJECT -> {
                                throw new StreamReadException(p, "Unexpected token: " + t);
                            }
                            case END_ARRAY -> {
                                break arrayLoop;
                            }
                            case VALUE_STRING -> value = new StringNode(p.getString());
                            case VALUE_NUMBER_INT -> value = _fromInt(p);
                            case VALUE_NUMBER_FLOAT -> value = _fromFloat(p);
                            case VALUE_TRUE -> value = TRUE;
                            case VALUE_FALSE -> value = FALSE;
                            case VALUE_NULL -> value = NULL;
                            default -> throw new StreamReadException(p, "Unexpected token: " + t);
                        }
                        currArray.add(value);
                    }
                    // Reached end of array (or input), so...
                }
            }

            // Either way, Object or Array ended, return up nesting level:
            curr = stack.popOrNull();
        } while (curr != null);

        return root;
    }

    private Node _deserializeAnyScalar(JsonParser p) throws StreamReadException {
        var currentToken = p.currentToken();
        return switch (currentToken) {
            case VALUE_STRING -> new StringNode(p.getString());
            case VALUE_NUMBER_INT -> _fromInt(p);
            case VALUE_NUMBER_FLOAT -> _fromFloat(p);
            case VALUE_TRUE -> TRUE;
            case VALUE_FALSE -> FALSE;
            case VALUE_NULL -> NULL;
            default -> throw new StreamReadException(p, "Unexpected token: " + currentToken);
        };
    }

    private Node _fromInt(JsonParser p) throws StreamReadException {
        var numberType = p.getNumberType();
        return switch (numberType) {
            case INT -> new IntNode(p.getIntValue());
            case LONG -> new LongNode(p.getLongValue());
            case BIG_INTEGER -> new BigIntegerNode(p.getBigIntegerValue());
            // 理论上永远不会发生
            default -> throw new StreamReadException(p, "Unsupported number type: " + numberType);
        };
    }

    private Node _fromFloat(JsonParser p) throws StreamReadException {
        var numberType = p.getNumberType();
        return switch (numberType) {
            case FLOAT -> new FloatNode(p.getFloatValue());
            case DOUBLE -> new DoubleNode(p.getDoubleValue());
            case BIG_DECIMAL -> new BigDecimalNode(p.getDecimalValue());
            // 理论上永远不会发生 todo NaN 怎么办?
            default -> throw new StreamReadException(p, "Unsupported number type: " + numberType);
        };
    }

    private void _handleDuplicateProperty(JsonParser p, String propName, ObjectNode objectNode, Node oldValue, Node newValue) throws JacksonException {
        // 注意此时 objectNode 中已经被新值覆盖了
        switch (_duplicateFieldPolicy) {
            case USE_NEW -> {
                // 什么都不做
            }
            case USE_OLD -> {
                objectNode.put(propName, oldValue);
            }
            case THROW -> {
                throw new StreamReadException(p, "检测到重复字段: \"" + propName + "\"");
            }
            case MERGE -> {
                if (oldValue instanceof ArrayNode arrayNode) {
                    arrayNode.add(newValue);
                    objectNode.put(propName, arrayNode);
                } else {
                    var arrayNode = new ArrayNode();
                    arrayNode.add(oldValue);
                    arrayNode.add(newValue);
                    objectNode.put(propName, arrayNode);
                }
            }
        }
    }

}
