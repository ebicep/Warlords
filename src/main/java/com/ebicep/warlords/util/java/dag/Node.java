package com.ebicep.warlords.util.java.dag;

import java.util.ArrayList;
import java.util.List;

public class Node<T> {

    private final List<Node<T>> children = new ArrayList<>();
    private T value;

    public Node(T value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Node{" +
                "children=" + children +
                ", value=" + value +
                '}';
    }

    public List<Node<T>> getChildren() {
        return children;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
