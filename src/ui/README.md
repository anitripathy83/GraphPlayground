# 🔷 Graph Theory Playground

An interactive **Graph Theory Playground** built entirely in Java (Swing) that lets you visually create graphs and run classic algorithms with step-by-step animation — right on your canvas.

---

## ✨ Features

### 🖱️ Canvas Interaction
- **Add Nodes** — click anywhere on the canvas to place a node
- **Add Edges** — click two nodes to connect them; enter a custom weight in the popup
- **Delete** — click any node or edge to remove it
- **Move Nodes** — drag nodes to rearrange your graph

### 🧠 Algorithms (all animated)
| Algorithm | Description |
|---|---|
| **BFS** | Breadth-First Search — explores level by level using a Queue |
| **DFS** | Depth-First Search — explores deep first using a Stack |
| **Dijkstra's** | Shortest path between two nodes using edge weights |
| **Prim's MST** | Minimum Spanning Tree — cheapest way to connect all nodes |
| **Cycle Detector** | Detects cycles (Union-Find for undirected, DFS stack for directed) |

### 📋 Step-by-step Traversal Log
- Live notebook-style log updates as each node is visited
- Summary popup at the end of every traversal
- Toggle between **Auto Mode** (600ms timer) and **Manual Mode** (click Next Step yourself)

### 📊 Graph Info
- **Adjacency Matrix** viewer
- **Node Degree** shown on hover
- **Tree checker** — confirms if the graph is a valid tree

---

## 🗂️ Project Structure

```
GraphPlayground/
└── src/
    ├── model/
    │   ├── Node.java           # Node data — id, label, position, radius
    │   ├── Edge.java           # Edge data — source, target, weight, directed
    │   └── Graph.java          # Core graph — add/remove nodes & edges, adjacency
    ├── canvas/
    │   └── GraphCanvas.java    # Drawing + mouse interaction (click, drag, hover)
    ├── algorithms/
    │   ├── BFS.java            # Breadth-First Search with animation
    │   ├── DFS.java            # Depth-First Search with animation
    │   ├── Dijkstra.java       # Shortest path with weight support
    │   ├── CycleDetector.java  # Cycle detection + tree checker
    │   └── Prims.java          # Minimum Spanning Tree
    ├── ui/
    │   ├── ControlPanel.java   # Buttons, mode switcher, algorithm controls
    │   └── TraversalLog.java   # Live step-by-step traversal output panel
    └── Main.java               # Entry point — wires everything together
```

---

## 🚀 Getting Started

### Prerequisites
- Java 11 or higher (project built with Java 21)
- No build tools required — plain Java only

### Run it

```bash
cd src
javac -cp . model\Node.java model\Edge.java model\Graph.java canvas\GraphCanvas.java algorithms\BFS.java algorithms\DFS.java algorithms\Dijkstra.java algorithms\CycleDetector.java algorithms\Prims.java ui\TraversalLog.java ui\ControlPanel.java Main.java
java -cp . Main
```

> On Mac/Linux replace `\` with `/` in the compile command.

---

## 🎮 How to Use

1. **Draw your graph**
   - Make sure **➕ Add Node** mode is selected
   - Click on the canvas to place nodes
   - Switch to **🔗 Add Edge** mode
   - Click two nodes — enter an edge weight in the popup (press Enter for default weight of 1)

2. **Run an algorithm**
   - Click **▶ BFS** or **▶ DFS** and pick a start node
   - Watch nodes turn **orange** (current) → **green** (visited)
   - Read the live log in the Traversal Log panel on the right

3. **Step through manually**
   - Toggle **🖐 Manual Mode: ON**
   - Start BFS or DFS
   - Click **⏭ Next Step** to advance one node at a time

4. **Find shortest path**
   - Click **📍 Dijkstra**, pick source and target
   - Shortest path lights up in **gold**

5. **Find Minimum Spanning Tree**
   - Click **🌲 Prim's MST**, pick a start node
   - MST edges highlighted in gold, total cost shown in popup

6. **Check graph properties**
   - **🔄 Cycle Check** — detects cycles and checks if it's a tree
   - **📊 Degree Info** — shows degree of every node
   - **🔢 Adjacency Matrix** — opens a scrollable matrix view

---

## 🎨 Color Guide

| Color | Meaning |
|---|---|
| 🔵 Blue | Default unvisited node |
| 🟠 Orange | Currently processing node |
| 🟢 Green | Visited node |
| 🟡 Gold | Shortest path / MST edge |

---

## 📚 Concepts Covered

This project is a hands-on implementation of core **Discrete Mathematics** and **Data Structures** topics:

- Graph representation (adjacency list + matrix)
- BFS and DFS traversal
- Shortest path (Dijkstra's algorithm)
- Minimum Spanning Tree (Prim's algorithm)
- Cycle detection (Union-Find + DFS recursion stack)
- Tree properties and connectivity

---

## 🛠️ Built With

- **Java 21** (Temurin)
- **Java Swing** — UI and canvas rendering
- **No external libraries or build tools**

---

## 📄 License

This project is open source and available under the [MIT License](LICENSE).