package com.ebicep.warlords.game.option.towerdefense.path;

import com.ebicep.warlords.util.java.dag.DAGUtils;
import com.ebicep.warlords.util.java.dag.DirectAcyclicGraph;
import com.ebicep.warlords.util.java.dag.Node;
import org.bukkit.Location;

import java.util.*;

public class TowerDefenseDirectAcyclicGraph extends DirectAcyclicGraph<Location, TowerDefenseDirectAcyclicGraph.TowerDefenseEdge> {

    private final Map<Integer, Node<Location>> nodeIndex = new HashMap<>();
    private final Map<Node<Location>, Double> nodeDistanceToEnd = new HashMap<>();

    public TowerDefenseDirectAcyclicGraph(Location spawn) {
        super(spawn);
    }

    public TowerDefenseDirectAcyclicGraph(Node<Location> root) {
        super(root);
        nodeIndex.put(root.hashCode(), root);
    }

    public void calculateEdgeData() {
        for (Node<Location> node : getEdges().keySet()) {
            for (TowerDefenseEdge edge : getEdges().get(node)) {
                Location from = node.getValue();
                Location to = edge.getTo().getValue();
                edge.distance = from.distance(to);
                edge.pathDirection = PathDirection.getPathDirection(from, to);
            }
        }
    }

    public void calculateNodeDistances() {
        Node<Location> endNode = null;
        for (Node<Location> child : getRoot().getChildren()) {
            List<TowerDefenseEdge> edges = getEdges(child);
            if (edges == null || edges.isEmpty()) {
                endNode = child;
                break;
            }
        }
        if (endNode == null) {
            return;
        }
        Set<Node<Location>> allNodes = new HashSet<>();
        DAGUtils.depthFirstSearch(this, getRoot(), allNodes);
        for (Node<Location> node : allNodes) {
            Double distance = DAGUtils.getDistance(this, node, endNode, TowerDefenseDirectAcyclicGraph.TowerDefenseEdge::getDistance);
            nodeDistanceToEnd.put(node, distance != null ? distance : Double.MAX_VALUE);
        }
    }

    @SafeVarargs
    public final TowerDefenseDirectAcyclicGraph addNodes(Node<Location>... node) {
        for (Node<Location> n : node) {
            nodeIndex.put(n.hashCode(), n);
            addNode(n);
        }
        return this;
    }

    public TowerDefenseDirectAcyclicGraph addEdge(Node<Location> from, Node<Location> to) {
        addEdge(from, new TowerDefenseDirectAcyclicGraph.TowerDefenseEdge(to));
        return this;
    }


    public Map<Integer, Node<Location>> getNodeIndex() {
        return nodeIndex;
    }

    public Map<Node<Location>, Double> getNodeDistanceToEnd() {
        return nodeDistanceToEnd;
    }

    public static class TowerDefenseEdge extends Edge<Location> {

        private PathDirection pathDirection;
        private double distance;

        public TowerDefenseEdge(Node<Location> to) {
            super(to);
        }

        public PathDirection getPathDirection() {
            return pathDirection;
        }

        public double getDistance() {
            return distance;
        }

        @Override
        public String toString() {
            return "TowerDefenseEdge{" +
                    "to=" + getTo() +
                    ", pathDirection=" + pathDirection +
                    ", distance=" + distance +
                    '}';
        }
    }
}
