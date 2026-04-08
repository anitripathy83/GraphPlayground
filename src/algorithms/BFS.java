package algorithms;

import canvas.GraphCanvas;
import model.Graph;
import model.Node;
import ui.TraversalLog;

import javax.swing.JOptionPane;
import javax.swing.Timer;
import java.util.*;

public class BFS {

    private final Graph graph;
    private final GraphCanvas canvas;
    private final TraversalLog log;

    // Step-by-step state
    private Queue<Node> queue;
    private Set<Node>   visited;
    private Set<Node>   current;
    private int[]       step;
    private Timer       autoTimer;
    private boolean     running = false;

    public BFS(Graph graph, GraphCanvas canvas, TraversalLog log) {
        this.graph  = graph;
        this.canvas = canvas;
        this.log    = log;
    }

    // ── Called by Run button ─────────────────────────────────
    public void start(Node startNode, boolean autoMode) {
        if (startNode == null) return;

        // Stop any previous run
        stop();

        visited = new LinkedHashSet<>();
        current = new HashSet<>();
        queue   = new LinkedList<>();
        step    = new int[]{1};

        queue.add(startNode);
        visited.add(startNode);
        running = true;

        log.startTraversal("BFS", startNode.getLabel());
        canvas.clearHighlights();

        if (autoMode) {
            autoTimer = new Timer(600, e -> {
                if (!nextStep()) {
                    autoTimer.stop();
                }
            });
            autoTimer.start();
        }
        // In manual mode — wait for nextStep() calls from button
    }

    // ── Called by Next Step button ───────────────────────────
    public boolean nextStep() {
        if (!running) return false;

        if (queue.isEmpty()) {
            current.clear();
            canvas.setCurrentNodes(current);
            canvas.repaint();
            log.endTraversal(step[0] - 1);
            running = false;

            JOptionPane.showMessageDialog(canvas,
                log.getFullLog(),
                "BFS Complete",
                JOptionPane.INFORMATION_MESSAGE);
            return false;
        }

        Node node = queue.poll();
        current.clear();
        current.add(node);

        log.logStep(step[0]++, node.getLabel());

        for (Node neighbor : graph.getNeighbors(node)) {
            if (!visited.contains(neighbor)) {
                visited.add(neighbor);
                queue.add(neighbor);
            }
        }

        canvas.setVisitedNodes(visited);
        canvas.setCurrentNodes(current);
        canvas.repaint();
        return true;
    }

    public void stop() {
        if (autoTimer != null && autoTimer.isRunning()) autoTimer.stop();
        running = false;
    }

    public boolean isRunning() { return running; }
}