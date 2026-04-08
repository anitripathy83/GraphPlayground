import canvas.GraphCanvas;
import model.Graph;
import ui.ControlPanel;

import javax.swing.*;
import java.awt.*;

public class Main {

    public static void main(String[] args) {
        // Always build Swing UI on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            Graph graph = new Graph();
            GraphCanvas canvas = new GraphCanvas(graph);
            ControlPanel controlPanel = new ControlPanel(graph, canvas);

            JFrame frame = new JFrame("Graph Theory Playground");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());

            // Canvas fills the center, control panel on the right
            frame.add(canvas, BorderLayout.CENTER);
            frame.add(controlPanel, BorderLayout.EAST);

            frame.pack();
            frame.setLocationRelativeTo(null); // center on screen
            frame.setResizable(true);
            frame.setVisible(true);
        });
    }
}
