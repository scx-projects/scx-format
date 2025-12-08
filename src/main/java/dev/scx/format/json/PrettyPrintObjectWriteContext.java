package dev.scx.format.json;

import tools.jackson.core.*;
import tools.jackson.core.io.CharacterEscapes;
import tools.jackson.core.tree.ArrayTreeNode;
import tools.jackson.core.tree.ObjectTreeNode;
import tools.jackson.core.util.DefaultPrettyPrinter;

/// PrettyPrintObjectWriteContext
///
/// @author scx567888
/// @version 0.0.1
final class PrettyPrintObjectWriteContext implements ObjectWriteContext {

    public static final PrettyPrintObjectWriteContext PRETTY_PRINT_OBJECT_WRITE_CONTEXT = new PrettyPrintObjectWriteContext();

    private final PrettyPrinter prettyPrinter;

    private PrettyPrintObjectWriteContext() {
        this.prettyPrinter = new DefaultPrettyPrinter();
    }

    @Override
    public FormatSchema getSchema() {
        return null;
    }

    @Override
    public CharacterEscapes getCharacterEscapes() {
        return null;
    }

    @Override
    public PrettyPrinter getPrettyPrinter() {
        return prettyPrinter;
    }

    @Override
    public boolean hasPrettyPrinter() {
        return true;
    }

    @Override
    public SerializableString getRootValueSeparator(SerializableString defaultSeparator) {
        return defaultSeparator;
    }

    @Override
    public int getStreamWriteFeatures(int defaults) {
        return defaults;
    }

    @Override
    public int getFormatWriteFeatures(int defaults) {
        return defaults;
    }

    @Override
    public TokenStreamFactory tokenStreamFactory() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ArrayTreeNode createArrayNode() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ObjectTreeNode createObjectNode() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void writeValue(JsonGenerator g, Object value) throws JacksonException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void writeTree(JsonGenerator g, TreeNode value) throws JacksonException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
