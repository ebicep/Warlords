package com.ebicep.warlords.game.option.towerdefense.path;

import com.ebicep.warlords.util.java.dag.DirectAcyclicGraph;
import com.ebicep.warlords.util.java.dag.Node;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;

public class TowerDefenseDirectAcyclicGraph extends DirectAcyclicGraph<Location, TowerDefenseDirectAcyclicGraph.TowerDefenseEdge> {

    private final Map<Integer, Node<Location>> nodeIndex = new HashMap<>();

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
