
import java.util.*;

public class astar1<V> extends AbstractGraph<V> {
    
    private Map<V, Map<V, Double>> heuristicMap;
    
    public astar1() {
        super();
        this.heuristicMap = new HashMap<>();
    }
    
    public astar1(V[] vertices, int[][] edges) {
        super(vertices, edges);
        this.heuristicMap = new HashMap<>();
    }
    
    public astar1(List<V> vertices, List<Edge> edges) {
        super(vertices, edges);
        this.heuristicMap = new HashMap<>();
    }
    
    public astar1(List<Edge> edges, int numberOfVertices) {
        super(edges, numberOfVertices);
        this.heuristicMap = new HashMap<>();
    }
    
    public astar1(int[][] edges, int numberOfVertices) {
        super(edges, numberOfVertices);
        this.heuristicMap = new HashMap<>();
    }
    
    /**
     * Add a weighted edge (bus route with distance)
     */
    public boolean addWeightedEdge(V u, V v, double weight) {
        int indexU = getIndex(u);
        int indexV = getIndex(v);
        if (indexU == -1 || indexV == -1) return false;
        return addEdge(indexU, indexV, weight);
    }
    
    /**
     * Add heuristic value - Java 8 compatible
     */
    public void addHeuristic(V from, V to, double value) {
        if (!heuristicMap.containsKey(from)) {
            heuristicMap.put(from, new HashMap<V, Double>());
        }
        heuristicMap.get(from).put(to, value);
        
        if (!heuristicMap.containsKey(to)) {
            heuristicMap.put(to, new HashMap<V, Double>());
        }
        heuristicMap.get(to).put(from, value);
    }
    
    /**
     * Get heuristic value
     */
    private double getHeuristic(V from, V to) {
        if (heuristicMap.containsKey(from) && heuristicMap.get(from).containsKey(to)) {
            return heuristicMap.get(from).get(to);
        }
        return 0;
    }
    
    /**
     * A* algorithm - finds shortest path using heuristic
     */
    public Tree astar1(V start, V end) {
        int startIndex = getIndex(start);
        int endIndex = getIndex(end);
        
        if (startIndex == -1 || endIndex == -1) return null;
        
        int size = vertices.size();
        double[] gScore = new double[size];
        int[] parent = new int[size];
        boolean[] visited = new boolean[size];
        List<Integer> searchOrder = new ArrayList<>();
        
        Arrays.fill(gScore, Double.MAX_VALUE);
        Arrays.fill(parent, -1);
        gScore[startIndex] = 0;
        
        PriorityQueue<NodeFScore> openSet = new PriorityQueue<>();
        openSet.add(new NodeFScore(startIndex, getHeuristic(start, end)));
        
        while (!openSet.isEmpty()) {
            NodeFScore current = openSet.poll();
            int u = current.index;
            
            if (visited[u]) continue;
            visited[u] = true;
            searchOrder.add(u);
            
            if (u == endIndex) {
                return new Tree(startIndex, parent, searchOrder);
            }
            
            for (Edge e : neighbors.get(u)) {
                int v = e.v;
                if (!visited[v]) {
                    double weight = getWeight(u, v);
                    double tentativeG = gScore[u] + weight;
                    if (tentativeG < gScore[v]) {
                        gScore[v] = tentativeG;
                        parent[v] = u;
                        double fScore = tentativeG + getHeuristic(vertices.get(v), end);
                        openSet.add(new NodeFScore(v, fScore));
                    }
                }
            }
        }
        
        return gScore[endIndex] == Double.MAX_VALUE ? null : new Tree(startIndex, parent, searchOrder);
    }
    
    /**
     * Get path from tree
     */
    public List<V> getPathFromTree(Tree tree, V start, V end) {
        if (tree == null) return null;
        
        int startIndex = getIndex(start);
        int endIndex = getIndex(end);
        if (startIndex == -1 || endIndex == -1) return null;
        
        List<V> path = new ArrayList<>();
        int current = endIndex;
        while (current != startIndex) {
            path.add(vertices.get(current));
            current = tree.getParent(current);
            if (current == -1) return null;
        }
        path.add(vertices.get(startIndex));
        Collections.reverse(path);
        return path;
    }
    
    /**
     * Print path with arrows (no symbols)
     */
    public void printPath(List<V> path) {
        if (path == null || path.isEmpty()) {
            System.out.println("No path found!");
            return;
        }
        
        System.out.print("Path: ");
        for (int i = 0; i < path.size(); i++) {
            System.out.print(path.get(i));
            if (i < path.size() - 1) {
                System.out.print(" -> ");
            }
        }
        System.out.println();
    }
    
    /**
     * Print detailed route with distances and heuristics (no symbols)
     */
    public void printRouteDetails(List<V> path, V end) {
        if (path == null || path.size() < 2) {
            System.out.println("No route found!");
            return;
        }
        
        System.out.println("\nRoute Details:");
        System.out.println("-".repeat(60));
        
        double totalDistance = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            V from = path.get(i);
            V to = path.get(i + 1);
            double dist = getWeight(from, to);
            double heuristic = getHeuristic(to, end);
            totalDistance += dist;
            System.out.printf("  %d. %-20s -> %-20s (%.1f KM, h=%.1f)%n", 
                i + 1, from, to, dist, heuristic);
        }
        
        System.out.println("-".repeat(60));
        System.out.printf("Total Distance: %.1f KM%n", totalDistance);
        System.out.printf("Number of Stops: %d%n", getStops(path));
    }
    
    /**
     * Print A* traversal result (no symbols)
     */
    public void printastar1Tree(Tree tree, V start, V end) {
        if (tree == null) {
            System.out.println("No A* tree found!");
            return;
        }
        
        System.out.println("\n" + "=".repeat(70));
        System.out.println("A* TRAVERSAL from " + start + " to " + end);
        System.out.println("=".repeat(70));
        
        System.out.println("\nSearch Order:");
        for (int i = 0; i < tree.getSearchOrder().size(); i++) {
            System.out.print(vertices.get(tree.getSearchOrder().get(i)));
            if (i < tree.getSearchOrder().size() - 1) {
                System.out.print(" -> ");
            }
        }
        System.out.println();
        
        System.out.println("\nTree Structure:");
        tree.printTree();
        
        System.out.println("\nNumber of vertices found: " + tree.getNumberOfVerticesFound());
    }

    /**
     * NodeFScore inner class for PriorityQueue
     */
    private static class NodeFScore implements Comparable<NodeFScore> {
        int index;
        double fScore;
        
        NodeFScore(int index, double fScore) {
            this.index = index;
            this.fScore = fScore;
        }
        
        @Override
        public int compareTo(NodeFScore other) {
            return Double.compare(this.fScore, other.fScore);
        }
    }
}