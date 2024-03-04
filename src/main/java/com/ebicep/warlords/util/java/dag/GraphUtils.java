package com.ebicep.warlords.util.java.dag;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GraphUtils {

    public record NodeDistance<T>(Node<T> node, double distance) {
    }

    public static void depthFirstSearch(DirectAcyclicGraph<?, ?> graph, Node<?> node, Set<Node<?>> visited) {
        visited.add(node);
        List<? extends DirectAcyclicGraph.Edge<?>> edges = graph.getEdges().get(node);
        if (edges == null) {
            return;
        }
        for (DirectAcyclicGraph.Edge<?> edge : edges) {
            Node<?> to = edge.getTo();
            if (!visited.contains(to)) {
                depthFirstSearch(graph, to, visited);
            }
        }
    }

    public static int countNumberOfReachableNodes(DirectAcyclicGraph<?, ?> graph, Node<?> node) {
        Set<Node<?>> visited = new HashSet<>();
        depthFirstSearch(graph, node, visited);
        return visited.size() - 1;
    }

    public static <T, R extends DirectAcyclicGraph.Edge<T>> double dijkstra(
            DirectAcyclicGraph<T, R> graph,
            Node<T> start,
            Node<T> end,
            Function<R, Double> weightFunction
    ) {
        int numberOfReachableNodes = countNumberOfReachableNodes(graph, start);
        System.out.println("Number of reachable nodes: " + numberOfReachableNodes);
        Set<Node<T>> visited = new HashSet<>();
        Map<Node<T>, Double> distances = graph.getEdges()
                                              .keySet()
                                              .stream()
                                              .collect(Collectors.toMap(node -> node, node -> Double.MAX_VALUE));
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
                if (visited.contains(edge.getTo())) {
                    continue;
                }
                double newDistance = distances.get(current) + weightFunction.apply(edge);
                if (newDistance < distances.get(edge.getTo())) {
                    distances.put(edge.getTo(), newDistance);
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
