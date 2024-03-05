package com.ebicep.warlords.util.java.dag;

import java.util.*;
import java.util.function.Function;

public class DAGUtils {

    public static <T, R extends DirectAcyclicGraph.Edge<T>> void depthFirstSearch(DirectAcyclicGraph<T, R> graph, Node<T> node, Set<Node<T>> visited) {
        visited.add(node);
        List<R> edges = graph.getEdges().get(node);
        if (edges == null) {
            return;
        }
        for (R edge : edges) {
            Node<T> to = edge.getTo();
            if (!visited.contains(to)) {
                depthFirstSearch(graph, to, visited);
            }
        }
    }

    public static <T, R extends DirectAcyclicGraph.Edge<T>> int countNumberOfReachableNodes(DirectAcyclicGraph<T, R> graph, Node<T> node) {
        Set<Node<T>> visited = new HashSet<>();
        depthFirstSearch(graph, node, visited);
        return visited.size() - 1;
    }

    public static <T, R extends DirectAcyclicGraph.Edge<T>> Double getDistance(
            DirectAcyclicGraph<T, R> graph,
            Node<T> start,
            Node<T> end,
            Function<R, Double> weightFunction
    ) {
        int numberOfReachableNodes = countNumberOfReachableNodes(graph, start);
        Set<Node<T>> visited = new HashSet<>();
        Map<Node<T>, Double> distances = new HashMap<>();
        distances.put(start, 0D);
        Node<T> current = start;
        // while not all nodes are visited
        for (int i = 0; i < numberOfReachableNodes; i++) {
            visited.add(current);
            // get vertex outside of visited with smallest distance
            List<R> edges = graph.getEdges().get(current);
            if (edges == null) {
                continue;
            }
            for (R edge : edges) {
                Node<T> nodeTo = edge.getTo();
                if (visited.contains(nodeTo)) {
                    continue;
                }
                double newDistance = distances.get(current) + weightFunction.apply(edge);
                Double previousDistance = distances.get(nodeTo);
                if (previousDistance == null || newDistance < previousDistance) {
                    distances.put(nodeTo, newDistance);
                }
            }
            current = distances.entrySet()
                               .stream()
                               .filter(entry -> !visited.contains(entry.getKey()))
                               .min(Map.Entry.comparingByValue())
                               .map(Map.Entry::getKey)
                               .orElse(null);
        }

        return distances.get(end);
    }
}
