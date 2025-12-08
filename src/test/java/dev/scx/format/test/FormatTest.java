package dev.scx.format.test;

import dev.scx.format.FormatToNodeException;
import dev.scx.format.NodeToFormatException;
import dev.scx.format.json.JsonNodeConvertOptions;
import dev.scx.format.json.JsonNodeConverter;
import dev.scx.format.xml.XmlNodeConverter;
import dev.scx.format.xml.XmlNodeConverterOptions;
import org.testng.annotations.Test;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.dataformat.xml.XmlMapper;

public class FormatTest {

    private final static String json = """
        {
          "user": {
            "id": 12345,
            "name": "小明",
            "nickname": "明哥\\uD83D\\ude80",
            "active": true,
            "score": 99.99,
            "address": {
              "city": "北京",
              "zipcode": "100000",
              "coordinates": {
                "lat": 39.9042,
                "lng": 116.4074
              }
            },
            "tags": ["程序员", "摄影师", "旅行者"],
            "metadata": {
              "created_at": "2025-07-09T12:34:56Z",
              "updated_at": null,
              "roles": ["admin", "editor", {"custom": "superuser"}]
            }
          },
          "posts": [
            {
              "id": "post-001",
              "title": "第一篇文章",
              "content": "这是第一篇文章的内容，包含一些 <b>HTML</b> 标签。",
              "comments": [
                {"user": "小红", "message": "写得很好！"},
                {"user": "小刚", "message": "赞👍"}
              ]
            },
            {
              "id": "post-002",
              "title": "第二篇文章",
              "content": "这是第二篇文章，内容更丰富。",
              "comments": [[1, 2, 3], [4, 5, 6]]
            }
          ],
          "config": {
            "theme": "dark",
            "notifications": {
              "email": true,
              "sms": false,
              "push": true
            },
            "experimental": [true, false, null, "beta"]
          },
          "misc": [
            123,
            "字符串",
            null,
            {
              "nested": {
                "array": [1, 2, 3, {"deep": "value"}]
              }
            }
          ]
        }
        """;

    private static final JsonNodeConverter jsonNodeConverter = new JsonNodeConverter();
    private static final XmlNodeConverter xmlNodeConverter = new XmlNodeConverter();

    private static final ObjectMapper jsonMapper = new JsonMapper();
    private static final ObjectMapper xmlMapper = new XmlMapper();

    public static void main(String[] args) throws FormatToNodeException, NodeToFormatException {
        test1();
        test2();
    }

    @Test
    public static void test1() throws FormatToNodeException, NodeToFormatException {

        var node = jsonNodeConverter.formatToNode(json, new JsonNodeConvertOptions());

        var xml = xmlNodeConverter.nodeToFormatString(node, new XmlNodeConverterOptions());

        var node1 = xmlNodeConverter.formatToNode(xml, new XmlNodeConverterOptions());

        var json1 = jsonNodeConverter.nodeToFormatString(node1, new JsonNodeConvertOptions());

    }

    @Test
    public static void test2() throws JacksonException {

        var node = jsonMapper.readTree(json);

        var xml = xmlMapper.writeValueAsString(node);

        var node1 = xmlMapper.readTree(xml);

        var json1 = jsonMapper.writeValueAsString(node1);

    }

}
