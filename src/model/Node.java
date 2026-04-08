package model;

public class Node {
    private static int counter = 0;

    private final int id;
    private String label;
    private int x, y;
    private static final int RADIUS = 20;

    public Node(int x, int y) {
        this.id = counter++;
        this.label = String.valueOf(this.id);
        this.x = x;
        this.y = y;
    }

    public boolean contains(int mx, int my) {
        int dx = mx - x;
        int dy = my - y;
        return (dx * dx + dy * dy) <= (RADIUS * RADIUS);
    }

    public int getId()       { return id; }
    public String getLabel() { return label; }
    public int getX()        { return x; }
    public int getY()        { return y; }
    public int getRadius()   { return RADIUS; }

    public void setX(int x)          { this.x = x; }
    public void setY(int y)          { this.y = y; }
    public void setLabel(String l)   { this.label = l; }

    public static void resetCounter() { counter = 0; }

    @Override
    public String toString() {
        return "Node(" + id + ", " + label + ")";
    }
}