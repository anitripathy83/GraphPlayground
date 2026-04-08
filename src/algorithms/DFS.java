package algorithms;

import canvas.GraphCanvas;
import model.Graph;
import model.Node;
import ui.TraversalLog;

import javax.swing.JOptionPane;
import javax.swing.Timer;
import java.util.*;

public class DFS {

    private final Graph graph;
    private final GraphCanvas canvas;
    private final TraversalLog log;

    // Step-by-step state
    private Deque<Node> stack;
    private Set<Node>   visited;
    private Set<Node>   current;
    private int[]       step;
    private Timer       autoTimer;
    private boolean     running = false;

    public DFS(Graph graph, GraphCanvas canvas, TraversalLog log) {
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
        stack   = new ArrayDeque<>();
        step    = new int[]{1};

        stack.push(startNode);
        running = true;

        log.startTraversal("DFS", startNode.getLabel());
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

        while (!stack.isEmpty() && visited.contains(stack.peek())) {
            stack.pop();
        }

        if (stack.isEmpty()) {
            current.clear();
            canvas.setCurrentNodes(current);
            canvas.repaint();
            log.endTraversal(step[0] - 1);
            running = false;

            JOptionPane.showMessageDialog(canvas,
                log.getFullLog(),
                "DFS Complete",
                JOptionPane.INFORMATION_MESSAGE);
            return false;
        }

        Node node = stack.pop();
        visited.add(node);
        current.clear();
        current.add(node);

        log.logStep(step[0]++, node.getLabel());

        List<Node> neighbors = graph.getNeighbors(node);
        for (int i = neighbors.size() - 1; i >= 0; i--) {
            if (!visited.contains(neighbors.get(i))) {
                stack.push(neighbors.get(i));
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