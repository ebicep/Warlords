package com.ebicep.warlords.util.java.dag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DirectAcyclicGraph<T, R extends DirectAcyclicGraph.Edge<T>> {

    private final Node<T> root;
    private final Map<Node<T>, List<R>> edges = new HashMap<>();

    public DirectAcyclicGraph(T rootValue) {
        this.root = new Node<>(rootValue);
    }

    public DirectAcyclicGraph(Node<T> root) {
        this.root = root;
    }

    public Node<T> getRoot() {
        return root;
    }

    public void addNode(Node<T> node) {
        root.getChildren().add(node);
    }

    public void addEdge(Node<T> from, R edge) {
        edges.computeIfAbsent(from, k -> new ArrayList<>()).add(edge);
    }

    public List<R> getEdges(Node<T> from) {
        return edges.get(from);
    }

    public Map<Node<T>, List<R>> getEdges() {
        return edges;
    }

    public static class Edge<T> {
        private final Node<T> to;

        public Edge(Node<T> to) {
            this.to = to;
        }

        public Node<T> getTo() {
            return to;
        }

        @Override
        public String toString() {
            return "Edge{" +
                    "to=" + to +
                    '}';
        }
    }

}
