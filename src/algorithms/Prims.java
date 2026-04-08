package algorithms;

import canvas.GraphCanvas;
import model.Edge;
import model.Graph;
import model.Node;
import ui.TraversalLog;

import javax.swing.JOptionPane;
import javax.swing.Timer;
import java.util.*;

public class Prims {

    private final Graph graph;
    private final GraphCanvas canvas;
    private final TraversalLog log;

    public Prims(Graph graph, GraphCanvas canvas, TraversalLog log) {
        this.graph  = graph;
        this.canvas = canvas;
        this.log    = log;
    }

    public void start(Node startNode) {
        if (startNode == null) return;

        List<Node> allNodes = graph.getNodes();
        if (allNodes.size() < 2) {
            JOptionPane.showMessageDialog(canvas,
                "Need at least 2 nodes to run MST.",
                "Prim's MST", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // inMST = nodes already added to the MST
        Set<Node> inMST        = new LinkedHashSet<>();
        Set<Edge> mstEdges     = new LinkedHashSet<>();
        Set<Node> visitedAnim  = new LinkedHashSet<>();
        Set<Node> currentAnim  = new HashSet<>();

        inMST.add(startNode);
        visitedAnim.add(startNode);

        log.startTraversal("Prim's MST", startNode.getLabel());
        canvas.clearHighlights();

        int[] step      = {1};
        int[] totalCost = {0};

        Timer timer = new Timer(700, null);

        timer.addActionListener(e -> {
            // If all nodes are in MST — done
            if (inMST.size() == allNodes.size()) {
                currentAnim.clear();
                canvas.setCurrentNodes(currentAnim);
                canvas.setVisitedNodes(visitedAnim);
                canvas.setHighlightEdges(mstEdges);
                canvas.repaint();

                log.endTraversal(step[0] - 1);
                timer.stop();

                JOptionPane.showMessageDialog(canvas,
                    log.getFullLog() + "\nTotal MST cost: " + totalCost[0],
                    "Prim's MST Complete",
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Find the cheapest edge connecting inMST to a node outside MST
            Edge cheapest = null;
            for (Edge edge : graph.getEdges()) {
                Node src = edge.getSource();
                Node tgt = edge.getTarget();

                boolean srcIn = inMST.contains(src);
                boolean tgtIn = inMST.contains(tgt);

                // One end inside MST, other end outside
                if (srcIn && !tgtIn || tgtIn && !srcIn) {
                    if (cheapest == null || edge.getWeight() < cheapest.getWeight()) {
                        cheapest = edge;
                    }
                }
            }

            // No connecting edge found — graph might be disconnected
            if (cheapest == null) {
                log.endTraversal(step[0] - 1);
                timer.stop();
                JOptionPane.showMessageDialog(canvas,
                    "Graph is disconnected — MST cannot span all nodes.\n"
                    + "Partial MST cost: " + totalCost[0],
                    "Prim's MST", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Add the new node to MST
            Node newNode = inMST.contains(cheapest.getSource())
                ? cheapest.getTarget()
                : cheapest.getSource();

            inMST.add(newNode);
            mstEdges.add(cheapest);
            totalCost[0] += cheapest.getWeight();

            currentAnim.clear();
            currentAnim.add(newNode);
            visitedAnim.add(newNode);

            log.logStep(step[0]++, newNode.getLabel()
                + " (edge weight: " + cheapest.getWeight() + ")");

            canvas.setVisitedNodes(visitedAnim);
            canvas.setCurrentNodes(currentAnim);
            canvas.setHighlightEdges(mstEdges);
            canvas.repaint();
        });

        timer.start();
    }
}