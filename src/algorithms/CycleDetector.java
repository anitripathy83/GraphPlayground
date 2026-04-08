package algorithms;

import model.Graph;
import model.Node;

import java.util.*;

public class CycleDetector {

    private final Graph graph;

    public CycleDetector(Graph graph) {
        this.graph = graph;
    }

    // ── Main entry point ─────────────────────────────────────
    public boolean hasCycle() {
        if (graph.getNodes().isEmpty()) return false;

        // Check if any edge is directed — use different algorithm
        boolean isDirected = graph.getEdges().stream()
                .anyMatch(e -> e.isDirected());

        return isDirected ? hasCycleDirected() : hasCycleUndirected();
    }

    // ── Undirected: Union-Find algorithm ─────────────────────
    private boolean hasCycleUndirected() {
        Map<Node, Node> parent = new HashMap<>();

        // Each node starts as its own parent
        for (Node n : graph.getNodes()) {
            parent.put(n, n);
        }

        for (model.Edge e : graph.getEdges()) {
            Node rootA = find(parent, e.getSource());
            Node rootB = find(parent, e.getTarget());

            // If both nodes share the same root — adding this edge creates a cycle
            if (rootA == rootB) return true;

            // Union: merge the two sets
            parent.put(rootA, rootB);
        }

        return false;
    }

    // Find root of a node's set (with path compression)
    private Node find(Map<Node, Node> parent, Node node) {
        if (parent.get(node) != node) {
            parent.put(node, find(parent, parent.get(node))); // path compression
        }
        return parent.get(node);
    }

    // ── Directed: DFS with recursion stack ───────────────────
    private boolean hasCycleDirected() {
        Set<Node> visited   = new HashSet<>();
        Set<Node> recStack  = new HashSet<>();  // nodes in current DFS path

        for (Node n : graph.getNodes()) {
            if (!visited.contains(n)) {
                if (dfsDirected(n, visited, recStack)) return true;
            }
        }
        return false;
    }

    private boolean dfsDirected(Node node, Set<Node> visited, Set<Node> recStack) {
        visited.add(node);
        recStack.add(node);

        for (Node neighbor : graph.getNeighbors(node)) {
            if (!visited.contains(neighbor)) {
                if (dfsDirected(neighbor, visited, recStack)) return true;
            } else if (recStack.contains(neighbor)) {
                // Found a back edge — cycle exists
                return true;
            }
        }

        recStack.remove(node); // done with this path
        return false;
    }

    // ── Extra info ───────────────────────────────────────────

    // A graph is a tree if it's connected, has no cycle, and edges = nodes - 1
    public boolean isTree() {
        if (graph.getNodes().isEmpty()) return false;
        if (hasCycle()) return false;
        if (graph.getEdgeCount() != graph.getNodeCount() - 1) return false;
        return isConnected();
    }

    private boolean isConnected() {
        List<Node> nodes = graph.getNodes();
        if (nodes.isEmpty()) return true;

        Set<Node> visited = new HashSet<>();
        Queue<Node> queue = new LinkedList<>();
        queue.add(nodes.get(0));
        visited.add(nodes.get(0));

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            for (Node neighbor : graph.getNeighbors(current)) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }

        return visited.size() == nodes.size();
    }

    // Returns degree info for all nodes as a formatted string
    public String getDegreeInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("Node Degrees:\n");
        for (Node n : graph.getNodes()) {
            sb.append("  Node ").append(n.getLabel())
              .append(" → degree: ").append(graph.getDegree(n))
              .append("\n");
        }
        return sb.toString();
    }
}
