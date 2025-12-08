package dev.scx.format.json;

import dev.scx.node.ContainerNode;

import java.util.Arrays;

/// ContainerStack
///
/// @author scx567888
/// @version 0.0.1
final class ContainerStack {

    private ContainerNode[] _stack;
    private int _top;
    private int _end;

    public ContainerStack() {

    }

    // Not used yet but useful for limits (fail at [some high depth])
    public int size() {
        return _top;
    }

    public void push(ContainerNode node) {
        if (_top < _end) {
            _stack[_top++] = node; // lgtm [java/dereferenced-value-may-be-null]
            return;
        }
        if (_stack == null) {
            _end = 10;
            _stack = new ContainerNode[_end];
        } else {
            // grow by 50%, for most part
            _end += Math.min(4000, Math.max(20, _end >> 1));
            _stack = Arrays.copyOf(_stack, _end);
        }
        _stack[_top++] = node;
    }

    public ContainerNode popOrNull() {
        if (_top == 0) {
            return null;
        }
        // note: could clean up stack but due to usage pattern, should not make
        // any difference -- all nodes joined during and after construction and
        // after construction the whole stack is discarded
        return _stack[--_top];
    }

}
