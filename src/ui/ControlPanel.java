package ui;

import algorithms.BFS;
import algorithms.CycleDetector;
import algorithms.DFS;
import algorithms.Dijkstra;
import algorithms.Prims;
import canvas.GraphCanvas;
import model.Graph;
import model.Node;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;

public class ControlPanel extends JPanel {

    private final Graph graph;
    private final GraphCanvas canvas;

    private final BFS bfs;
    private final DFS dfs;
    private final Dijkstra dijkstra;
    private final CycleDetector cycleDetector;
    private final Prims prims;
    private final TraversalLog traversalLog;

    private boolean autoMode   = true;
    private String  activeAlgo = null;

    private JButton       btnNextStep;
    private JToggleButton btnAutoToggle;

    public ControlPanel(Graph graph, GraphCanvas canvas) {
        this.graph  = graph;
        this.canvas = canvas;

        this.traversalLog  = new TraversalLog();
        this.bfs           = new BFS(graph, canvas, traversalLog);
        this.dfs           = new DFS(graph, canvas, traversalLog);
        this.dijkstra      = new Dijkstra(graph, canvas);
        this.cycleDetector = new CycleDetector(graph);
        this.prims         = new Prims(graph, canvas, traversalLog);

        setLayout(new BorderLayout(5, 5));
        setPreferredSize(new Dimension(260, 700));
        setBackground(new Color(30, 30, 30));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(buildModePanel(), BorderLayout.NORTH);
        add(buildAlgoPanel(), BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setBackground(new Color(30, 30, 30));
        southPanel.add(traversalLog,     BorderLayout.CENTER);
        southPanel.add(buildInfoPanel(), BorderLayout.SOUTH);
        add(southPanel, BorderLayout.SOUTH);
    }

    // ── Mode buttons ─────────────────────────────────────────
    private JPanel buildModePanel() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 5, 5));
        panel.setBackground(new Color(30, 30, 30));
        panel.setBorder(titledBorder("Mode"));

        panel.add(modeButton("➕ Add Node",  GraphCanvas.Mode.ADD_NODE));
        panel.add(modeButton("🔗 Add Edge",  GraphCanvas.Mode.ADD_EDGE));
        panel.add(modeButton("🗑 Delete",    GraphCanvas.Mode.DELETE));
        panel.add(modeButton("✋ Move Node", GraphCanvas.Mode.MOVE));

        return panel;
    }

    // ── Algorithm buttons ────────────────────────────────────
    private JPanel buildAlgoPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.setBackground(new Color(30, 30, 30));
        panel.setBorder(titledBorder("Algorithms"));

        // Auto / Manual toggle
        btnAutoToggle = new JToggleButton("⏩ Auto Mode: ON");
        btnAutoToggle.setSelected(true);
        btnAutoToggle.setBackground(new Color(50, 120, 50));
        btnAutoToggle.setForeground(Color.WHITE);
        btnAutoToggle.setFocusPainted(false);
        btnAutoToggle.setBorderPainted(false);
        btnAutoToggle.setFont(new Font("Arial", Font.PLAIN, 12));
        btnAutoToggle.addActionListener(e -> {
            autoMode = btnAutoToggle.isSelected();
            if (btnAutoToggle.isSelected()) {
                btnAutoToggle.setText("⏩ Auto Mode: ON");
                btnAutoToggle.setBackground(new Color(50, 120, 50));
                btnNextStep.setEnabled(false);
            } else {
                btnAutoToggle.setText("🖐 Manual Mode: ON");
                btnAutoToggle.setBackground(new Color(120, 80, 20));
                btnNextStep.setEnabled(true);
            }
        });

        // Next Step button
        btnNextStep = styledButton("⏭ Next Step");
        btnNextStep.setEnabled(false);
        btnNextStep.addActionListener(e -> {
            if ("BFS".equals(activeAlgo))      bfs.nextStep();
            else if ("DFS".equals(activeAlgo)) dfs.nextStep();
            else showInfo("Start BFS or DFS in manual mode first.");
        });

        // BFS
        JButton btnBFS = styledButton("▶ BFS");
        btnBFS.addActionListener(e -> {
            Node start = pickNode("Select START node for BFS:");
            if (start == null) return;
            activeAlgo = "BFS";
            dfs.stop();
            bfs.start(start, autoMode);
            if (!autoMode) showInfo("Manual mode — click ⏭ Next Step");
        });

        // DFS
        JButton btnDFS = styledButton("▶ DFS");
        btnDFS.addActionListener(e -> {
            Node start = pickNode("Select START node for DFS:");
            if (start == null) return;
            activeAlgo = "DFS";
            bfs.stop();
            dfs.start(start, autoMode);
            if (!autoMode) showInfo("Manual mode — click ⏭ Next Step");
        });

        // Dijkstra
        JButton btnDijkstra = styledButton("📍 Dijkstra");
        btnDijkstra.addActionListener(e -> {
            Node source = pickNode("Select SOURCE node:");
            if (source == null) return;
            Node target = pickNode("Select TARGET node:");
            if (target != null) {
                bfs.stop(); dfs.stop();
                activeAlgo = null;
                dijkstra.start(source, target);
            }
        });

        // Prim's MST
        JButton btnMST = styledButton("🌲 Prim's MST");
        btnMST.addActionListener(e -> {
            Node start = pickNode("Select START node for MST:");
            if (start == null) return;
            bfs.stop(); dfs.stop();
            activeAlgo = null;
            prims.start(start);
        });

        // Cycle check
        JButton btnCycle = styledButton("🔄 Cycle Check");
        btnCycle.addActionListener(e -> {
            boolean cycle = cycleDetector.hasCycle();
            boolean tree  = cycleDetector.isTree();
            showInfo("Cycle detected: " + (cycle ? "YES ⚠️" : "NO ✅")
                + "\nIs a Tree: " + (tree ? "YES 🌲" : "NO"));
        });

        // Degree info
        JButton btnDegree = styledButton("📊 Degree Info");
        btnDegree.addActionListener(e -> showInfo(cycleDetector.getDegreeInfo()));

        // Adjacency matrix
        JButton btnMatrix = styledButton("🔢 Adjacency Matrix");
        btnMatrix.addActionListener(e -> showAdjacencyMatrix());

        // Clear
        JButton btnClear = styledButton("🧹 Clear All");
        btnClear.setBackground(new Color(180, 50, 50));
        btnClear.addActionListener(e -> {
            bfs.stop(); dfs.stop();
            activeAlgo = null;
            graph.clear();
            canvas.clearHighlights();
            showInfo("Canvas cleared.");
        });

        panel.add(btnAutoToggle);
        panel.add(btnNextStep);
        panel.add(btnBFS);
        panel.add(btnDFS);
        panel.add(btnDijkstra);
        panel.add(btnMST);
        panel.add(btnCycle);
        panel.add(btnDegree);
        panel.add(btnMatrix);
        panel.add(Box.createVerticalStrut(10));
        panel.add(btnClear);

        return panel;
    }

    // ── Info panel ───────────────────────────────────────────
    private JPanel buildInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(30, 30, 30));
        panel.setBorder(titledBorder("Info"));

        JTextArea area = new JTextArea(4, 18);
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 11));
        area.setBackground(new Color(20, 20, 20));
        area.setForeground(new Color(180, 255, 180));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setText("Welcome!\nClick canvas to add nodes.\nThen switch to Add Edge mode.");

        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(240, 90));
        panel.add(scroll, BorderLayout.CENTER);
        panel.putClientProperty("infoArea", area);
        return panel;
    }

    // ── Helpers ──────────────────────────────────────────────
    private JButton modeButton(String label, GraphCanvas.Mode mode) {
        JButton btn = styledButton(label);
        btn.addActionListener(e -> {
            canvas.setMode(mode);
            showInfo("Mode: " + mode.name());
        });
        return btn;
    }

    private JButton styledButton(String label) {
        JButton btn = new JButton(label);
        btn.setBackground(new Color(60, 60, 60));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Arial", Font.PLAIN, 12));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (btn.isEnabled()) btn.setBackground(new Color(90, 90, 90));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (btn.isEnabled()) btn.setBackground(new Color(60, 60, 60));
            }
        });
        return btn;
    }

    private Node pickNode(String prompt) {
        List<Node> nodes = graph.getNodes();
        if (nodes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No nodes on canvas!",
                "Error", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        String[] labels = nodes.stream().map(Node::getLabel).toArray(String[]::new);
        String chosen = (String) JOptionPane.showInputDialog(
            this, prompt, "Select Node",
            JOptionPane.PLAIN_MESSAGE, null, labels, labels[0]);
        if (chosen == null) return null;
        return nodes.stream()
            .filter(n -> n.getLabel().equals(chosen))
            .findFirst().orElse(null);
    }

    private void showAdjacencyMatrix() {
        List<Node> nodes = graph.getNodes();
        if (nodes.isEmpty()) { showInfo("No nodes yet."); return; }

        int[][] matrix = graph.getAdjacencyMatrix();
        StringBuilder sb = new StringBuilder("Adjacency Matrix:\n\n     ");
        for (Node n : nodes) sb.append(String.format("%-4s", n.getLabel()));
        sb.append("\n");
        for (int i = 0; i < nodes.size(); i++) {
            sb.append(String.format("%-5s", nodes.get(i).getLabel()));
            for (int j = 0; j < nodes.size(); j++) {
                sb.append(String.format("%-4d", matrix[i][j]));
            }
            sb.append("\n");
        }

        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        textArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(textArea);
        scroll.setPreferredSize(new Dimension(400, 300));
        JOptionPane.showMessageDialog(this, scroll,
            "Adjacency Matrix", JOptionPane.PLAIN_MESSAGE);
    }

    private void showInfo(String text) {
        JPanel south     = (JPanel) getComponent(2);
        JPanel infoPanel = (JPanel) south.getComponent(1);
        JTextArea area   = (JTextArea) infoPanel.getClientProperty("infoArea");
        if (area != null) area.setText(text);
    }

    private TitledBorder titledBorder(String title) {
        TitledBorder border = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80)), title);
        border.setTitleColor(Color.LIGHT_GRAY);
        return border;
    }
}