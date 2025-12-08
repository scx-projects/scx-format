package dev.scx.format.json;

import dev.scx.format.FormatNodeConvertOptions;
import tools.jackson.core.*;
import tools.jackson.core.io.CharacterEscapes;
import tools.jackson.core.json.JsonReadFeature;
import tools.jackson.core.json.JsonWriteFeature;
import tools.jackson.core.util.DefaultPrettyPrinter;

import static dev.scx.format.json.JacksonHelper.configure;
import static tools.jackson.core.StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION;
import static tools.jackson.core.StreamWriteFeature.WRITE_BIGDECIMAL_AS_PLAIN;
import static tools.jackson.core.json.JsonReadFeature.*;
import static tools.jackson.core.json.JsonWriteFeature.*;

/// JsonNodeConvertOptions
///
/// @author scx567888
/// @version 0.0.1
public class JsonNodeConvertOptions implements FormatNodeConvertOptions {

    private final static int DEFAULT_FACTORY_FEATURE_FLAGS = TokenStreamFactory.Feature.collectDefaults();
    private final static int DEFAULT_STREAM_READ_FEATURE_FLAGS = StreamReadFeature.collectDefaults();
    private final static int DEFAULT_STREAM_WRITE_FEATURE_FLAGS = StreamWriteFeature.collectDefaults();
    private final static int DEFAULT_JSON_PARSER_FEATURE_FLAGS = JsonReadFeature.collectDefaults();
    private final static int DEFAULT_JSON_GENERATOR_FEATURE_FLAGS = JsonWriteFeature.collectDefaults();

    private final static SerializableString DEFAULT_ROOT_VALUE_SEPARATOR = DefaultPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR;
    private final static char DEFAULT_QUOTE_CHAR = '"';

    // _streamReadFeatures 相关
    private boolean includeSourceInLocation;

    // _streamWriteFeatures 相关
    private boolean writeBigDecimalAsPlain;

    // _formatReadFeatures 相关
    private boolean allowJavaComments;
    private boolean allowYamlComments;
    private boolean allowSingleQuotes;
    private boolean allowUnquotedPropertyNames;
    private boolean allowLeadingDecimalPointForNumbers;
    private boolean allowLeadingPlusSignForNumbers;
    private boolean allowLeadingZerosForNumbers;
    private boolean allowNonNumericNumbers;
    private boolean allowTrailingDecimalPointForNumbers;
    private boolean allowMissingValues;
    private boolean allowTrailingComma;

    // _formatWriteFeatures 相关
    private boolean quotePropertyNames;
    private boolean writeNanAsStrings;
    private boolean escapeNonAscii;
    private boolean writeNumbersAsStrings;

    // StreamReadConstraints/StreamWriteConstraints 相关
    private int maxNestingDepth;
    private long maxDocumentLength;
    private long maxTokenCount;
    private int maxNumberLength;
    private int maxStringLength;
    private int maxNameLength;

    // ErrorReportConfiguration 相关
    private ErrorReportConfiguration errorReportConfiguration;

    // 自定义配置
    private DuplicateFieldPolicy duplicateFieldPolicy;
    private boolean prettyPrint;

    // other
    private CharacterEscapes characterEscapes;
    private SerializableString rootValueSeparator;
    private int highestNonEscapedChar;
    private char quoteChar;

    public JsonNodeConvertOptions() {
        // _streamReadFeatures 相关
        this.includeSourceInLocation = INCLUDE_SOURCE_IN_LOCATION.enabledByDefault();
        // _streamWriteFeatures 相关
        this.writeBigDecimalAsPlain = WRITE_BIGDECIMAL_AS_PLAIN.enabledByDefault();
        // _formatReadFeatures 相关
        this.allowJavaComments = ALLOW_JAVA_COMMENTS.enabledByDefault();
        this.allowYamlComments = ALLOW_YAML_COMMENTS.enabledByDefault();
        this.allowSingleQuotes = ALLOW_SINGLE_QUOTES.enabledByDefault();
        this.allowUnquotedPropertyNames = ALLOW_UNQUOTED_PROPERTY_NAMES.enabledByDefault();
        this.allowLeadingDecimalPointForNumbers = ALLOW_LEADING_DECIMAL_POINT_FOR_NUMBERS.enabledByDefault();
        this.allowLeadingPlusSignForNumbers = ALLOW_LEADING_PLUS_SIGN_FOR_NUMBERS.enabledByDefault();
        this.allowLeadingZerosForNumbers = ALLOW_LEADING_ZEROS_FOR_NUMBERS.enabledByDefault();
        this.allowNonNumericNumbers = ALLOW_NON_NUMERIC_NUMBERS.enabledByDefault();
        this.allowTrailingDecimalPointForNumbers = ALLOW_TRAILING_DECIMAL_POINT_FOR_NUMBERS.enabledByDefault();
        this.allowMissingValues = ALLOW_MISSING_VALUES.enabledByDefault();
        this.allowTrailingComma = ALLOW_TRAILING_COMMA.enabledByDefault();
        // _formatWriteFeatures 相关
        this.quotePropertyNames = QUOTE_PROPERTY_NAMES.enabledByDefault();
        this.writeNanAsStrings = WRITE_NAN_AS_STRINGS.enabledByDefault();
        this.escapeNonAscii = ESCAPE_NON_ASCII.enabledByDefault();
        this.writeNumbersAsStrings = WRITE_NUMBERS_AS_STRINGS.enabledByDefault();
        // StreamReadConstraints/StreamWriteConstraints 相关
        this.maxNestingDepth = StreamReadConstraints.DEFAULT_MAX_DEPTH;
        this.maxDocumentLength = StreamReadConstraints.DEFAULT_MAX_DOC_LEN;
        this.maxTokenCount = StreamReadConstraints.DEFAULT_MAX_TOKEN_COUNT;
        this.maxNumberLength = StreamReadConstraints.DEFAULT_MAX_NUM_LEN;
        this.maxStringLength = StreamReadConstraints.DEFAULT_MAX_STRING_LEN;
        this.maxNameLength = StreamReadConstraints.DEFAULT_MAX_NAME_LEN;
        // ErrorReportConfiguration 相关
        this.errorReportConfiguration = ErrorReportConfiguration.defaults();
        // 自定义配置
        this.duplicateFieldPolicy = DuplicateFieldPolicy.USE_NEW;
        this.prettyPrint = false;
        // other
        this.characterEscapes = null;
        this.rootValueSeparator = DEFAULT_ROOT_VALUE_SEPARATOR;
        this.highestNonEscapedChar = 0; // disabled
        this.quoteChar = DEFAULT_QUOTE_CHAR;
    }

    public boolean includeSourceInLocation() {
        return includeSourceInLocation;
    }

    public JsonNodeConvertOptions includeSourceInLocation(boolean includeSourceInLocation) {
        this.includeSourceInLocation = includeSourceInLocation;
        return this;
    }

    public boolean writeBigDecimalAsPlain() {
        return writeBigDecimalAsPlain;
    }

    public JsonNodeConvertOptions writeBigDecimalAsPlain(boolean writeBigDecimalAsPlain) {
        this.writeBigDecimalAsPlain = writeBigDecimalAsPlain;
        return this;
    }

    public boolean allowJavaComments() {
        return allowJavaComments;
    }

    public JsonNodeConvertOptions allowJavaComments(boolean allowJavaComments) {
        this.allowJavaComments = allowJavaComments;
        return this;
    }

    public boolean allowYamlComments() {
        return allowYamlComments;
    }

    public JsonNodeConvertOptions allowYamlComments(boolean allowYamlComments) {
        this.allowYamlComments = allowYamlComments;
        return this;
    }

    public boolean allowSingleQuotes() {
        return allowSingleQuotes;
    }

    public JsonNodeConvertOptions allowSingleQuotes(boolean allowSingleQuotes) {
        this.allowSingleQuotes = allowSingleQuotes;
        return this;
    }

    public boolean allowUnquotedPropertyNames() {
        return allowUnquotedPropertyNames;
    }

    public JsonNodeConvertOptions allowUnquotedPropertyNames(boolean allowUnquotedPropertyNames) {
        this.allowUnquotedPropertyNames = allowUnquotedPropertyNames;
        return this;
    }

    public boolean allowLeadingDecimalPointForNumbers() {
        return allowLeadingDecimalPointForNumbers;
    }

    public JsonNodeConvertOptions allowLeadingDecimalPointForNumbers(boolean allowLeadingDecimalPointForNumbers) {
        this.allowLeadingDecimalPointForNumbers = allowLeadingDecimalPointForNumbers;
        return this;
    }

    public boolean allowLeadingPlusSignForNumbers() {
        return allowLeadingPlusSignForNumbers;
    }

    public JsonNodeConvertOptions allowLeadingPlusSignForNumbers(boolean allowLeadingPlusSignForNumbers) {
        this.allowLeadingPlusSignForNumbers = allowLeadingPlusSignForNumbers;
        return this;
    }

    public boolean allowLeadingZerosForNumbers() {
        return allowLeadingZerosForNumbers;
    }

    public JsonNodeConvertOptions allowLeadingZerosForNumbers(boolean allowLeadingZerosForNumbers) {
        this.allowLeadingZerosForNumbers = allowLeadingZerosForNumbers;
        return this;
    }

    public boolean allowNonNumericNumbers() {
        return allowNonNumericNumbers;
    }

    public JsonNodeConvertOptions allowNonNumericNumbers(boolean allowNonNumericNumbers) {
        this.allowNonNumericNumbers = allowNonNumericNumbers;
        return this;
    }

    public boolean allowTrailingDecimalPointForNumbers() {
        return allowTrailingDecimalPointForNumbers;
    }

    public JsonNodeConvertOptions allowTrailingDecimalPointForNumbers(boolean allowTrailingDecimalPointForNumbers) {
        this.allowTrailingDecimalPointForNumbers = allowTrailingDecimalPointForNumbers;
        return this;
    }

    public boolean allowMissingValues() {
        return allowMissingValues;
    }

    public JsonNodeConvertOptions allowMissingValues(boolean allowMissingValues) {
        this.allowMissingValues = allowMissingValues;
        return this;
    }

    public boolean allowTrailingComma() {
        return allowTrailingComma;
    }

    public JsonNodeConvertOptions allowTrailingComma(boolean allowTrailingComma) {
        this.allowTrailingComma = allowTrailingComma;
        return this;
    }

    public boolean quotePropertyNames() {
        return quotePropertyNames;
    }

    public JsonNodeConvertOptions quotePropertyNames(boolean quotePropertyNames) {
        this.quotePropertyNames = quotePropertyNames;
        return this;
    }

    public boolean writeNanAsStrings() {
        return writeNanAsStrings;
    }

    public JsonNodeConvertOptions writeNanAsStrings(boolean writeNanAsStrings) {
        this.writeNanAsStrings = writeNanAsStrings;
        return this;
    }

    public boolean escapeNonAscii() {
        return escapeNonAscii;
    }

    public JsonNodeConvertOptions escapeNonAscii(boolean escapeNonAscii) {
        this.escapeNonAscii = escapeNonAscii;
        return this;
    }

    public boolean writeNumbersAsStrings() {
        return writeNumbersAsStrings;
    }

    public JsonNodeConvertOptions writeNumbersAsStrings(boolean writeNumbersAsStrings) {
        this.writeNumbersAsStrings = writeNumbersAsStrings;
        return this;
    }

    public int maxNestingDepth() {
        return maxNestingDepth;
    }

    public JsonNodeConvertOptions maxNestingDepth(int maxNestingDepth) {
        this.maxNestingDepth = maxNestingDepth;
        return this;
    }

    public long maxDocumentLength() {
        return maxDocumentLength;
    }

    public JsonNodeConvertOptions maxDocumentLength(long maxDocumentLength) {
        this.maxDocumentLength = maxDocumentLength;
        return this;
    }

    public long maxTokenCount() {
        return maxTokenCount;
    }

    public JsonNodeConvertOptions maxTokenCount(long maxTokenCount) {
        this.maxTokenCount = maxTokenCount;
        return this;
    }

    public int maxNumberLength() {
        return maxNumberLength;
    }

    public JsonNodeConvertOptions maxNumberLength(int maxNumberLength) {
        this.maxNumberLength = maxNumberLength;
        return this;
    }

    public int maxStringLength() {
        return maxStringLength;
    }

    public JsonNodeConvertOptions maxStringLength(int maxStringLength) {
        this.maxStringLength = maxStringLength;
        return this;
    }

    public int maxNameLength() {
        return maxNameLength;
    }

    public JsonNodeConvertOptions maxNameLength(int maxNameLength) {
        this.maxNameLength = maxNameLength;
        return this;
    }

    public ErrorReportConfiguration errorReportConfiguration() {
        return errorReportConfiguration;
    }

    public JsonNodeConvertOptions errorReportConfiguration(ErrorReportConfiguration errorReportConfiguration) {
        this.errorReportConfiguration = errorReportConfiguration;
        return this;
    }

    public DuplicateFieldPolicy duplicateFieldPolicy() {
        return duplicateFieldPolicy;
    }

    public JsonNodeConvertOptions duplicateFieldPolicy(DuplicateFieldPolicy duplicateFieldPolicy) {
        this.duplicateFieldPolicy = duplicateFieldPolicy;
        return this;
    }

    public boolean prettyPrint() {
        return prettyPrint;
    }

    public JsonNodeConvertOptions prettyPrint(boolean prettyPrint) {
        this.prettyPrint = prettyPrint;
        return this;
    }

    public CharacterEscapes characterEscapes() {
        return characterEscapes;
    }

    public JsonNodeConvertOptions characterEscapes(CharacterEscapes characterEscapes) {
        this.characterEscapes = characterEscapes;
        return this;
    }

    public SerializableString rootValueSeparator() {
        return rootValueSeparator;
    }

    public JsonNodeConvertOptions rootValueSeparator(SerializableString rootValueSeparator) {
        this.rootValueSeparator = rootValueSeparator;
        return this;
    }

    public int highestNonEscapedChar() {
        return highestNonEscapedChar;
    }

    public JsonNodeConvertOptions highestNonEscapedChar(int highestNonEscapedChar) {
        this.highestNonEscapedChar = highestNonEscapedChar;
        return this;
    }

    public char quoteChar() {
        return quoteChar;
    }

    public JsonNodeConvertOptions quoteChar(char quoteChar) {
        this.quoteChar = quoteChar;
        return this;
    }

    int toFactoryFeatures() {
        // 对于 FactoryFeatures 我们永远返回默认值.
        return DEFAULT_FACTORY_FEATURE_FLAGS;
    }

    int toStreamReadFeatures() {
        var _streamReadFeatures = DEFAULT_STREAM_READ_FEATURE_FLAGS;
        _streamReadFeatures = configure(INCLUDE_SOURCE_IN_LOCATION, includeSourceInLocation, _streamReadFeatures);
        return _streamReadFeatures;
    }

    int toStreamWriteFeature() {
        var _streamWriteFeatures = DEFAULT_STREAM_WRITE_FEATURE_FLAGS;
        _streamWriteFeatures = configure(WRITE_BIGDECIMAL_AS_PLAIN, writeBigDecimalAsPlain, _streamWriteFeatures);
        return _streamWriteFeatures;
    }

    int toFormatReadFeatures() {
        var _formatReadFeatures = DEFAULT_JSON_PARSER_FEATURE_FLAGS;
        _formatReadFeatures = configure(ALLOW_JAVA_COMMENTS, allowJavaComments, _formatReadFeatures);
        _formatReadFeatures = configure(ALLOW_YAML_COMMENTS, allowYamlComments, _formatReadFeatures);
        _formatReadFeatures = configure(ALLOW_SINGLE_QUOTES, allowSingleQuotes, _formatReadFeatures);
        _formatReadFeatures = configure(ALLOW_UNQUOTED_PROPERTY_NAMES, allowUnquotedPropertyNames, _formatReadFeatures);
        _formatReadFeatures = configure(ALLOW_LEADING_DECIMAL_POINT_FOR_NUMBERS, allowLeadingDecimalPointForNumbers, _formatReadFeatures);
        _formatReadFeatures = configure(ALLOW_LEADING_PLUS_SIGN_FOR_NUMBERS, allowLeadingPlusSignForNumbers, _formatReadFeatures);
        _formatReadFeatures = configure(ALLOW_LEADING_ZEROS_FOR_NUMBERS, allowLeadingZerosForNumbers, _formatReadFeatures);
        _formatReadFeatures = configure(ALLOW_NON_NUMERIC_NUMBERS, allowNonNumericNumbers, _formatReadFeatures);
        _formatReadFeatures = configure(ALLOW_TRAILING_DECIMAL_POINT_FOR_NUMBERS, allowTrailingDecimalPointForNumbers, _formatReadFeatures);
        _formatReadFeatures = configure(ALLOW_MISSING_VALUES, allowMissingValues, _formatReadFeatures);
        _formatReadFeatures = configure(ALLOW_TRAILING_COMMA, allowTrailingComma, _formatReadFeatures);
        return _formatReadFeatures;
    }

    int toFormatWriteFeatures() {
        var _formatWriteFeatures = DEFAULT_JSON_GENERATOR_FEATURE_FLAGS;
        _formatWriteFeatures = configure(QUOTE_PROPERTY_NAMES, quotePropertyNames, _formatWriteFeatures);
        _formatWriteFeatures = configure(WRITE_NAN_AS_STRINGS, writeNanAsStrings, _formatWriteFeatures);
        _formatWriteFeatures = configure(ESCAPE_NON_ASCII, escapeNonAscii, _formatWriteFeatures);
        _formatWriteFeatures = configure(WRITE_NUMBERS_AS_STRINGS, writeNumbersAsStrings, _formatWriteFeatures);
        return _formatWriteFeatures;
    }

    StreamReadConstraints toStreamReadConstraints() {
        return StreamReadConstraints.builder()
            .maxNestingDepth(maxNestingDepth)
            .maxDocumentLength(maxDocumentLength)
            .maxTokenCount(maxTokenCount)
            .maxNumberLength(maxNumberLength)
            .maxStringLength(maxStringLength)
            .maxNameLength(maxNameLength)
            .build();
    }

    StreamWriteConstraints toStreamWriteConstraints() {
        return StreamWriteConstraints.builder()
            .maxNestingDepth(maxNestingDepth)
            .build();
    }

}
