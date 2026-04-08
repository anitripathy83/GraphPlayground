package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Graph {
    private final List<Node> nodes;
    private final List<Edge> edges;

    public Graph() {
        this.nodes = new ArrayList<>();
        this.edges = new ArrayList<>();
    }

    // ── Node operations ──────────────────────────────────────

    public Node addNode(int x, int y) {
        Node node = new Node(x, y);
        nodes.add(node);
        return node;
    }

    public void removeNode(Node node) {
        // Remove all edges connected to this node first
        edges.removeIf(e -> e.getSource() == node || e.getTarget() == node);
        nodes.remove(node);
    }

    // Returns the node at click position, or null if none
    public Node getNodeAt(int x, int y) {
        for (int i = nodes.size() - 1; i >= 0; i--) {
            if (nodes.get(i).contains(x, y)) return nodes.get(i);
        }
        return null;
    }

    // ── Edge operations ──────────────────────────────────────

    public Edge addEdge(Node source, Node target) {
        if (source == target) return null;                  // no self loops
        if (edgeExists(source, target)) return null;        // no duplicate edges
        Edge edge = new Edge(source, target);
        edges.add(edge);
        return edge;
    }

    public Edge addEdge(Node source, Node target, int weight, boolean directed) {
        if (source == target) return null;
        if (edgeExists(source, target)) return null;
        Edge edge = new Edge(source, target, weight, directed);
        edges.add(edge);
        return edge;
    }

    public void removeEdge(Edge edge) {
        edges.remove(edge);
    }

    public boolean edgeExists(Node a, Node b) {
        for (Edge e : edges) {
            if (e.connects(a, b)) return true;
        }
        return false;
    }

    // Returns the edge at click position, or null if none
    public Edge getEdgeAt(int x, int y) {
        for (Edge e : edges) {
            if (e.contains(x, y)) return e;
        }
        return null;
    }

    // ── Adjacency & degree ───────────────────────────────────

    public List<Node> getNeighbors(Node node) {
        List<Node> neighbors = new ArrayList<>();
        for (Edge e : edges) {
            if (e.getSource() == node) neighbors.add(e.getTarget());
            else if (!e.isDirected() && e.getTarget() == node) neighbors.add(e.getSource());
        }
        return neighbors;
    }

    public int getDegree(Node node) {
        int degree = 0;
        for (Edge e : edges) {
            if (e.getSource() == node || e.getTarget() == node) degree++;
        }
        return degree;
    }

    // Builds a 2D adjacency matrix from current nodes/edges
    public int[][] getAdjacencyMatrix() {
        int n = nodes.size();
        int[][] matrix = new int[n][n];

        Map<Integer, Integer> idToIndex = new HashMap<>();
        for (int i = 0; i < n; i++) {
            idToIndex.put(nodes.get(i).getId(), i);
        }

        for (Edge e : edges) {
            int i = idToIndex.get(e.getSource().getId());
            int j = idToIndex.get(e.getTarget().getId());
            matrix[i][j] = e.getWeight();
            if (!e.isDirected()) matrix[j][i] = e.getWeight();
        }

        return matrix;
    }

    // ── Clear ────────────────────────────────────────────────

    public void clear() {
        nodes.clear();
        edges.clear();
        Node.resetCounter();
    }

    // ── Getters ──────────────────────────────────────────────

    public List<Node> getNodes() { return nodes; }
    public List<Edge> getEdges() { return edges; }
    public int getNodeCount()    { return nodes.size(); }
    public int getEdgeCount()    { return edges.size(); }
}