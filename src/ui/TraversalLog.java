package ui;

import javax.swing.*;
import java.awt.*;

public class TraversalLog extends JPanel {

    private final JTextArea logArea;

    public TraversalLog() {
        setLayout(new BorderLayout());
        setBackground(new Color(20, 20, 20));
        setBorder(javax.swing.BorderFactory.createTitledBorder(
            javax.swing.BorderFactory.createLineBorder(new Color(80, 80, 80)),
            "Traversal Log"));
        ((javax.swing.border.TitledBorder) getBorder()).setTitleColor(Color.LIGHT_GRAY);

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        logArea.setBackground(new Color(15, 15, 15));
        logArea.setForeground(new Color(180, 255, 180));
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        logArea.setText("Run BFS or DFS to see\nstep-by-step traversal here.");

        JScrollPane scroll = new JScrollPane(logArea);
        scroll.setPreferredSize(new Dimension(260, 300));
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);

        // Clear button
        JButton btnClear = new JButton("Clear Log");
        btnClear.setBackground(new Color(60, 60, 60));
        btnClear.setForeground(Color.WHITE);
        btnClear.setFocusPainted(false);
        btnClear.setBorderPainted(false);
        btnClear.setFont(new Font("Arial", Font.PLAIN, 11));
        btnClear.addActionListener(e -> logArea.setText(""));
        add(btnClear, BorderLayout.SOUTH);
    }

    // Called once at the start of a traversal
    public void startTraversal(String algorithm, String startLabel) {
        logArea.setText("");
        append("━━━━━━━━━━━━━━━━━━━━━━━━━");
        append(algorithm + " Traversal");
        append("Start node: " + startLabel);
        append("━━━━━━━━━━━━━━━━━━━━━━━━━");
        append("");
    }

    // Called at each step during traversal
    public void logStep(int stepNumber, String nodeLabel) {
        append("Step " + stepNumber + ": Visit node " + nodeLabel);
    }

    // Called when traversal finishes
    public void endTraversal(int totalSteps) {
        append("");
        append("━━━━━━━━━━━━━━━━━━━━━━━━━");
        append("✅ Done! Visited " + totalSteps + " nodes");
        append("━━━━━━━━━━━━━━━━━━━━━━━━━");

        // Auto scroll to bottom
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    // Returns full log text for summary popup
    public String getFullLog() {
        return logArea.getText();
    }

    private void append(String text) {
        logArea.append(text + "\n");
        // Auto scroll to latest line
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }
}