package algorithms;

import canvas.GraphCanvas;
import model.Edge;
import model.Graph;
import model.Node;

import javax.swing.JOptionPane;
import javax.swing.Timer;
import java.util.*;

public class Dijkstra {

    private final Graph graph;
    private final GraphCanvas canvas;

    public Dijkstra(Graph graph, GraphCanvas canvas) {
        this.graph = graph;
        this.canvas = canvas;
    }

    public void start(Node source, Node target) {
        if (source == null || target == null) return;

        // dist maps each node to its shortest known distance from source
        Map<Node, Integer>  dist    = new HashMap<>();
        Map<Node, Node>     prev    = new HashMap<>();  // to reconstruct path
        Map<Node, Edge>     prevEdge = new HashMap<>(); // to highlight path edges
        Set<Node>           settled  = new LinkedHashSet<>();

        // Priority queue — always picks the node with smallest distance
        PriorityQueue<Node> pq = new PriorityQueue<>(
            Comparator.comparingInt(n -> dist.getOrDefault(n, Integer.MAX_VALUE))
        );

        // Initialize all distances to infinity
        for (Node n : graph.getNodes()) {
            dist.put(n, Integer.MAX_VALUE);
        }
        dist.put(source, 0);
        pq.add(source);

        // Pre-compute full Dijkstra (not animated step by step)
        while (!pq.isEmpty()) {
            Node u = pq.poll();
            if (settled.contains(u)) continue;
            settled.add(u);

            for (Edge e : graph.getEdges()) {
                Node neighbor = null;
                if (e.getSource() == u) neighbor = e.getTarget();
                else if (!e.isDirected() && e.getTarget() == u) neighbor = e.getSource();
                if (neighbor == null || settled.contains(neighbor)) continue;

                int newDist = dist.get(u) + e.getWeight();
                if (newDist < dist.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                    dist.put(neighbor, newDist);
                    prev.put(neighbor, u);
                    prevEdge.put(neighbor, e);
                    pq.add(neighbor);
                }
            }
        }

        // Reconstruct shortest path from target back to source
        List<Node> path      = new ArrayList<>();
        Set<Edge>  pathEdges = new HashSet<>();
        Node current = target;

        while (current != null) {
            path.add(0, current);
            Edge e = prevEdge.get(current);
            if (e != null) pathEdges.add(e);
            current = prev.get(current);
        }

        // If path doesn't start at source — no path exists
        if (path.isEmpty() || path.get(0) != source) {
            JOptionPane.showMessageDialog(canvas,
                "No path found from " + source.getLabel() + " to " + target.getLabel(),
                "Dijkstra", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Animate the path node by node
        Set<Node> visitedAnim = new LinkedHashSet<>();
        Set<Node> currentAnim = new HashSet<>();

        int totalCost = dist.get(target);

        Timer timer = new Timer(700, null);
        int[] step = {0}; // array trick to modify inside lambda

        timer.addActionListener(e -> {
            if (step[0] >= path.size()) {
                currentAnim.clear();
                canvas.setCurrentNodes(currentAnim);
                canvas.setHighlightEdges(pathEdges);
                canvas.repaint();
                timer.stop();

                JOptionPane.showMessageDialog(canvas,
                    "Shortest path: " + pathToString(path) + "\nTotal cost: " + totalCost,
                    "Dijkstra Result", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            Node node = path.get(step[0]);
            visitedAnim.add(node);
            currentAnim.clear();
            currentAnim.add(node);

            canvas.setVisitedNodes(visitedAnim);
            canvas.setCurrentNodes(currentAnim);
            canvas.setHighlightEdges(pathEdges);
            canvas.repaint();

            step[0]++;
        });

        canvas.clearHighlights();
        timer.start();
    }

    private String pathToString(List<Node> path) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < path.size(); i++) {
            sb.append(path.get(i).getLabel());
            if (i < path.size() - 1) sb.append(" → ");
        }
        return sb.toString();
    }
}