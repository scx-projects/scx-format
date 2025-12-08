package dev.scx.format.xml;

import dev.scx.format.FormatNodeConvertOptions;

/// XmlNodeConverterOptions
///
/// @author scx567888
/// @version 0.0.1
public class XmlNodeConverterOptions implements FormatNodeConvertOptions {

    /// 最大嵌套深度
    private int maxNestingDepth;
    /// 最大子元素数量 (同时作用于属性和子元素)
    private int maxChildCount;
    /// 最大字符串长度 (同时作用于属性值和文本)
    private int maxStringLength;
    /// 根节点名称
    private String rootName;
    /// 匿名元素 名称
    private String itemName;

    public XmlNodeConverterOptions() {
        this.maxNestingDepth = 200; // 默认 200 既不会轻易栈溢出, 也足够 99.99% 的情况
        this.maxChildCount = 5000;
        this.maxStringLength = 2000 * 10000;
        this.rootName = "root";
        this.itemName = "item";
    }

    public int maxNestingDepth() {
        return maxNestingDepth;
    }

    public XmlNodeConverterOptions maxNestingDepth(int maxNestingDepth) {
        if (maxNestingDepth < 0) {
            throw new IllegalArgumentException("maxNestingDepth cannot < 0");
        }
        this.maxNestingDepth = maxNestingDepth;
        return this;
    }

    public int maxChildCount() {
        return maxChildCount;
    }

    public XmlNodeConverterOptions maxChildCount(int maxChildCount) {
        if (maxChildCount < 0) {
            throw new IllegalArgumentException("maxChildCount cannot < 0");
        }
        this.maxChildCount = maxChildCount;
        return this;
    }

    public int maxStringLength() {
        return maxStringLength;
    }

    public XmlNodeConverterOptions maxStringLength(int maxStringLength) {
        if (maxStringLength < 0) {
            throw new IllegalArgumentException("maxStringLength cannot < 0");
        }
        this.maxStringLength = maxStringLength;
        return this;
    }

    public String rootName() {
        return rootName;
    }

    public XmlNodeConverterOptions rootName(String rootName) {
        if (rootName == null) {
            throw new NullPointerException("rootName cannot be null");
        }
        this.rootName = rootName;
        return this;
    }

    public String itemName() {
        return itemName;
    }

    public XmlNodeConverterOptions itemName(String itemName) {
        if (itemName == null) {
            throw new NullPointerException("itemName cannot be null");
        }
        this.itemName = itemName;
        return this;
    }

}
