package dev.scx.format.json;

/// 重复 Field 策略
///
/// @author scx567888
/// @version 0.0.1
public enum DuplicateFieldPolicy {

    /// 使用新值
    USE_NEW,

    /// 使用旧值
    USE_OLD,

    /// 抛出异常
    THROW,

    /// 合并为数组
    MERGE

}
