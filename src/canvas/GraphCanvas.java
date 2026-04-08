package canvas;

import model.Edge;
import model.Graph;
import model.Node;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Set;

public class GraphCanvas extends JPanel {

    // ── Interaction modes ────────────────────────────────────
    public enum Mode {
        ADD_NODE, ADD_EDGE, DELETE, MOVE
    }

    // ── Colors ───────────────────────────────────────────────
    public static final Color COLOR_NODE_DEFAULT  = new Color(70, 130, 180);
    public static final Color COLOR_NODE_SELECTED = new Color(255, 165, 0);
    public static final Color COLOR_NODE_VISITED  = new Color(50, 205, 50);
    public static final Color COLOR_NODE_CURRENT  = new Color(255, 69, 0);
    public static final Color COLOR_EDGE_DEFAULT  = new Color(100, 100, 100);
    public static final Color COLOR_EDGE_PATH     = new Color(255, 215, 0);
    public static final Color COLOR_BG            = new Color(245, 245, 245);

    // ── State ────────────────────────────────────────────────
    private final Graph graph;
    private Mode currentMode  = Mode.ADD_NODE;
    private Node selectedNode = null;
    private Node hoveredNode  = null;
    private Node draggedNode  = null;

    // Algorithm highlight sets
    private Set<Node> visitedNodes   = new HashSet<>();
    private Set<Node> currentNodes   = new HashSet<>();
    private Set<Edge> highlightEdges = new HashSet<>();

    // ── Constructor ──────────────────────────────────────────
    public GraphCanvas(Graph graph) {
        this.graph = graph;
        setBackground(COLOR_BG);
        setPreferredSize(new Dimension(900, 600));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleMousePressed(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                draggedNode = null;
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                hoveredNode = graph.getNodeAt(e.getX(), e.getY());
                repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (currentMode == Mode.MOVE && draggedNode != null) {
                    draggedNode.setX(e.getX());
                    draggedNode.setY(e.getY());
                    repaint();
                }
            }
        });
    }

    // ── Mouse press handler ──────────────────────────────────
    private void handleMousePressed(MouseEvent e) {
        int x = e.getX(), y = e.getY();
        Node clickedNode = graph.getNodeAt(x, y);

        switch (currentMode) {

            case ADD_NODE -> {
                if (clickedNode == null) {
                    graph.addNode(x, y);
                    repaint();
                }
            }

            case ADD_EDGE -> {
                if (clickedNode != null) {
                    if (selectedNode == null) {
                        selectedNode = clickedNode; // first node
                    } else {
                        // Second node clicked — ask for weight
                        int weight = askForWeight();
                        if (weight >= 0) {
                            graph.addEdge(selectedNode, clickedNode, weight, false);
                            repaint();
                        }
                        selectedNode = null;
                    }
                } else {
                    selectedNode = null; // clicked empty space, cancel
                }
            }

            case DELETE -> {
                if (clickedNode != null) {
                    graph.removeNode(clickedNode);
                } else {
                    Edge clickedEdge = graph.getEdgeAt(x, y);
                    if (clickedEdge != null) graph.removeEdge(clickedEdge);
                }
                repaint();
            }

            case MOVE -> {
                if (clickedNode != null) {
                    draggedNode = clickedNode;
                }
            }
        }
    }

    // ── Weight popup ─────────────────────────────────────────
    private int askForWeight() {
        while (true) {
            String input = JOptionPane.showInputDialog(
                this,
                "Enter edge weight (positive number):",
                "Edge Weight",
                JOptionPane.PLAIN_MESSAGE);

            // User cancelled
            if (input == null) return -1;

            input = input.trim();
            if (input.isEmpty()) return 1; // default weight

            try {
                int weight = Integer.parseInt(input);
                if (weight <= 0) {
                    JOptionPane.showMessageDialog(this,
                        "Weight must be a positive number. Try again.",
                        "Invalid Input", JOptionPane.WARNING_MESSAGE);
                } else {
                    return weight;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                    "Please enter a valid number.",
                    "Invalid Input", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    // ── Painting ─────────────────────────────────────────────
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawEdges(g2);
        drawNodes(g2);
        drawSelectedNodeIndicator(g2);
    }

    private void drawEdges(Graphics2D g2) {
        for (Edge e : graph.getEdges()) {
            if (highlightEdges.contains(e)) {
                g2.setColor(COLOR_EDGE_PATH);
                g2.setStroke(new BasicStroke(3));
            } else {
                g2.setColor(COLOR_EDGE_DEFAULT);
                g2.setStroke(new BasicStroke(2));
            }

            int x1 = e.getSource().getX(), y1 = e.getSource().getY();
            int x2 = e.getTarget().getX(), y2 = e.getTarget().getY();
            g2.drawLine(x1, y1, x2, y2);

            // Weight label at midpoint
            int mx = (x1 + x2) / 2, my = (y1 + y2) / 2;
            g2.setColor(new Color(50, 50, 50));
            g2.setFont(new Font("Arial", Font.BOLD, 12));
            g2.drawString(String.valueOf(e.getWeight()), mx + 4, my - 4);

            if (e.isDirected()) drawArrow(g2, x1, y1, x2, y2);
        }
    }

    private void drawNodes(Graphics2D g2) {
        for (Node n : graph.getNodes()) {
            int x = n.getX() - n.getRadius();
            int y = n.getY() - n.getRadius();
            int d = n.getRadius() * 2;

            if (currentNodes.contains(n))      g2.setColor(COLOR_NODE_CURRENT);
            else if (visitedNodes.contains(n)) g2.setColor(COLOR_NODE_VISITED);
            else if (n == selectedNode)         g2.setColor(COLOR_NODE_SELECTED);
            else if (n == hoveredNode)          g2.setColor(COLOR_NODE_SELECTED);
            else                                g2.setColor(COLOR_NODE_DEFAULT);

            g2.fillOval(x, y, d, d);

            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(2));
            g2.drawOval(x, y, d, d);

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 13));
            FontMetrics fm = g2.getFontMetrics();
            int lx = n.getX() - fm.stringWidth(n.getLabel()) / 2;
            int ly = n.getY() + fm.getAscent() / 2 - 2;
            g2.drawString(n.getLabel(), lx, ly);

            // Degree on hover
            if (n == hoveredNode) {
                g2.setColor(Color.DARK_GRAY);
                g2.setFont(new Font("Arial", Font.PLAIN, 11));
                g2.drawString("deg: " + graph.getDegree(n), n.getX() + 22, n.getY() - 10);
            }
        }
    }

    private void drawSelectedNodeIndicator(Graphics2D g2) {
        if (selectedNode != null && currentMode == Mode.ADD_EDGE) {
            g2.setColor(COLOR_NODE_SELECTED);
            g2.setStroke(new BasicStroke(3));
            int r = selectedNode.getRadius() + 5;
            g2.drawOval(selectedNode.getX() - r, selectedNode.getY() - r, r * 2, r * 2);
        }
    }

    private void drawArrow(Graphics2D g2, int x1, int y1, int x2, int y2) {
        double angle = Math.atan2(y2 - y1, x2 - x1);
        int r = 20;
        int ax = (int) (x2 - r * Math.cos(angle));
        int ay = (int) (y2 - r * Math.sin(angle));

        double arrowAngle = Math.toRadians(25);
        int arrowLen = 12;
        int lx = (int) (ax - arrowLen * Math.cos(angle - arrowAngle));
        int ly = (int) (ay - arrowLen * Math.sin(angle - arrowAngle));
        int rx = (int) (ax - arrowLen * Math.cos(angle + arrowAngle));
        int ry = (int) (ay - arrowLen * Math.sin(angle + arrowAngle));

        g2.setColor(COLOR_EDGE_DEFAULT);
        g2.setStroke(new BasicStroke(2));
        g2.drawLine(ax, ay, lx, ly);
        g2.drawLine(ax, ay, rx, ry);
    }

    // ── Public API for algorithms ────────────────────────────
    public void setVisitedNodes(Set<Node> visited)   { this.visitedNodes = visited; }
    public void setCurrentNodes(Set<Node> current)   { this.currentNodes = current; }
    public void setHighlightEdges(Set<Edge> edges)   { this.highlightEdges = edges; }

    public void clearHighlights() {
        visitedNodes.clear();
        currentNodes.clear();
        highlightEdges.clear();
        repaint();
    }

    public void setMode(Mode mode) {
        this.currentMode = mode;
        this.selectedNode = null;
        repaint();
    }

    public Mode getMode() { return currentMode; }
}