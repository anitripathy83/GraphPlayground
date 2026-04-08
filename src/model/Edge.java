package model;

public class Edge {
    private final Node source;
    private final Node target;
    private int weight;
    private final boolean directed;

    public Edge(Node source, Node target, int weight, boolean directed) {
        this.source = source;
        this.target = target;
        this.weight = weight;
        this.directed = directed;
    }

    // Convenience constructor — undirected, weight 1 by default
    public Edge(Node source, Node target) {
        this(source, target, 1, false);
    }

    // Check if this edge connects two given nodes
    public boolean connects(Node a, Node b) {
        if (directed) {
            return source == a && target == b;
        }
        return (source == a && target == b) || (source == b && target == a);
    }

    // Check if a mouse click lands near this edge's line
    public boolean contains(int mx, int my) {
        double x1 = source.getX(), y1 = source.getY();
        double x2 = target.getX(), y2 = target.getY();

        double length = Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
        if (length == 0) return false;

        // Distance from point (mx, my) to the line segment
        double distance = Math.abs((y2 - y1) * mx - (x2 - x1) * my + x2 * y1 - y2 * x1) / length;
        return distance <= 6; // 6px click tolerance
    }

    public Node getSource()  { return source; }
    public Node getTarget()  { return target; }
    public int getWeight()   { return weight; }
    public boolean isDirected() { return directed; }

    public void setWeight(int weight) { this.weight = weight; }

    @Override
    public String toString() {
        String arrow = directed ? " -> " : " -- ";
        return "Edge(" + source.getLabel() + arrow + target.getLabel() + ", w=" + weight + ")";
    }
}
