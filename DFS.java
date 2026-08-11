import java.util.*;

public class DFS<V> extends AbstractGraph<V> {

    public DFS() {
        super();
    }

    public DFS(V[] vertices, int[][] edges) {
        super(vertices, edges);
    }

    public DFS(List<V> vertices, List<Edge> edges) {
        super(vertices, edges);
    }

    public DFS(List<Edge> edges, int numberOfVertices) {
        super(edges, numberOfVertices);
    }

    public DFS(int[][] edges, int numberOfVertices) {
        super(edges, numberOfVertices);
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
     * Get the weight/distance between two vertices
     */
    public double getWeight(V u, V v) {
        int indexU = getIndex(u);
        int indexV = getIndex(v);
        if (indexU == -1 || indexV == -1) return Double.MAX_VALUE;
        return getWeight(indexU, indexV);
    }

    public double getWeight(int u, int v) {
        for (Edge edge : neighbors.get(u)) {
            if (edge.v == v) {
                if (edge instanceof WeightedEdge) {
                    return ((WeightedEdge) edge).weight;
                }
            }
        }
        return Double.MAX_VALUE;
    }

    public double getPathDistance(List<V> path) {
        if (path == null || path.size() < 2) return 0;
        double total = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            total += getWeight(path.get(i), path.get(i + 1));
        }
        return total;
    }

    public int getStops(List<V> path) {
        return path != null ? path.size() - 1 : 0;
    }

    // ============================================
    // EXTRA DFS METHODS (Not in AbstractGraph)
    // ============================================

    /**
     * Recursive DFS from start to destination
     * Stops when destination is found
     */
    public Tree dfsRecursive(V start, V end) {
        int startIndex = getIndex(start);
        int endIndex = getIndex(end);
        
        if (startIndex == -1 || endIndex == -1) return null;
        
        List<Integer> searchOrder = new ArrayList<>();
        int[] parent = new int[vertices.size()];
        Arrays.fill(parent, -1);
        boolean[] isVisited = new boolean[vertices.size()];
        
        boolean found = dfsRecursiveHelper(startIndex, endIndex, parent, searchOrder, isVisited);
        
        return found ? new Tree(startIndex, parent, searchOrder) : null;
    }

    private boolean dfsRecursiveHelper(int u, int target, int[] parent, 
                                       List<Integer> searchOrder, boolean[] isVisited) {
        searchOrder.add(u);
        isVisited[u] = true;
        
        if (u == target) return true;
        
        for (Edge e : neighbors.get(u)) {
            if (!isVisited[e.v]) {
                parent[e.v] = u;
                if (dfsRecursiveHelper(e.v, target, parent, searchOrder, isVisited)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Iterative DFS using Stack
     */
    public Tree dfsIterative(int v) {
        if (v < 0 || v >= vertices.size()) return null;
        
        List<Integer> searchOrder = new ArrayList<>();
        int[] parent = new int[vertices.size()];
        Arrays.fill(parent, -1);
        boolean[] isVisited = new boolean[vertices.size()];
        
        LinkedList<Integer> stack = new LinkedList<>();
        stack.push(v);
        isVisited[v] = true;
        
        while (!stack.isEmpty()) {
            int u = stack.pop();
            searchOrder.add(u);
            
            for (int i = neighbors.get(u).size() - 1; i >= 0; i--) {
                Edge e = neighbors.get(u).get(i);
                if (!isVisited[e.v]) {
                    stack.push(e.v);
                    parent[e.v] = u;
                    isVisited[e.v] = true;
                }
            }
        }
        
        return new Tree(v, parent, searchOrder);
    }

    /**
     * Iterative DFS from start to destination
     */
    public Tree dfsIterative(V start, V end) {
        int startIndex = getIndex(start);
        int endIndex = getIndex(end);
        
        if (startIndex == -1 || endIndex == -1) return null;
        
        List<Integer> searchOrder = new ArrayList<>();
        int[] parent = new int[vertices.size()];
        Arrays.fill(parent, -1);
        boolean[] isVisited = new boolean[vertices.size()];
        
        LinkedList<Integer> stack = new LinkedList<>();
        stack.push(startIndex);
        isVisited[startIndex] = true;
        
        while (!stack.isEmpty()) {
            int u = stack.pop();
            searchOrder.add(u);
            
            if (u == endIndex) {
                return new Tree(startIndex, parent, searchOrder);
            }
            
            for (int i = neighbors.get(u).size() - 1; i >= 0; i--) {
                Edge e = neighbors.get(u).get(i);
                if (!isVisited[e.v]) {
                    stack.push(e.v);
                    parent[e.v] = u;
                    isVisited[e.v] = true;
                }
            }
        }
        
        return null;
    }

    /**
     * Get path from tree
     */
    public List<V> getPathFromTree(Tree tree, V start, V end) {
        if (tree == null) return null;
        
        int startIndex = getIndex(start);
        int endIndex = getIndex(end);
        if (startIndex == -1 || endIndex == -1) return null;
        
        LinkedList<V> path = new LinkedList<>();
        int current = endIndex;
        while (current != startIndex) {
            path.addFirst(vertices.get(current));
            current = tree.getParent(current);
            if (current == -1) return null;
        }
        path.addFirst(vertices.get(startIndex));
        return path;
    }

    /**
     * Print DFS traversal result
     */
    public void printDFSTree(Tree tree, V start) {
        if (tree == null) {
            System.out.println("No DFS tree found!");
            return;
        }
        
        System.out.println("\n" + "=".repeat(70));
        System.out.println("DFS TRAVERSAL from " + start);
        System.out.println("=".repeat(70));
        
        System.out.println("\nSearch Order:");
        for (int i = 0; i < tree.getSearchOrder().size(); i++) {
            System.out.print(vertices.get(tree.getSearchOrder().get(i)));
            if (i < tree.getSearchOrder().size() - 1) {
                System.out.print(" → ");
            }
        }
        System.out.println();
        
        System.out.println("\nTree Structure:");
        tree.printTree();
        
        System.out.println("\nNumber of vertices found: " + tree.getNumberOfVerticesFound());
    }
  
    
}
