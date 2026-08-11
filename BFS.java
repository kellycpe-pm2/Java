import java.util.*;

public class BFS {
    private List<String> vertices = new ArrayList<>();
    private List<List<Edge>> neighbors = new ArrayList<>();
    
    public static class Edge {
        public int u;
        public int v;
        public double weight;
        
        public Edge(int u, int v, double weight) {
            this.u = u;
            this.v = v;
            this.weight = weight;
        }
    }
    
    public class Tree {
        private int root;
        private int[] parent;
        private List<Integer> searchOrder;
        
        public Tree(int root, int[] parent, List<Integer> searchOrder) {
            this.root = root;
            this.parent = parent;
            this.searchOrder = searchOrder;
        }
        
        public int getRoot() { return root; }
        public int[] getParent() { return parent; }
        public List<Integer> getSearchOrder() { return searchOrder; }
        public int getNumberOfVerticesFound() { return searchOrder.size(); }
    }
    
    /**
     * Add a vertex to the graph
     */
    public boolean addVertex(String vertex) {
        if (!vertices.contains(vertex)) {
            vertices.add(vertex);
            neighbors.add(new ArrayList<>());
            return true;
        }
        return false;
    }
    
    /**
     * Get the index of a vertex
     */
    public int getIndex(String vertex) {
        return vertices.indexOf(vertex);
    }
    
    /**
     * Get the vertex at a specific index
     */
    public String getVertex(int index) {
        return vertices.get(index);
    }
    
    /**
     * Add a weighted undirected edge
     */
    public boolean addEdge(String u, String v, double weight) {
        int indexU = getIndex(u);
        int indexV = getIndex(v);
        if (indexU == -1 || indexV == -1) return false;
        
        neighbors.get(indexU).add(new Edge(indexU, indexV, weight));
        neighbors.get(indexV).add(new Edge(indexV, indexU, weight));
        return true;
    }
    
    /**
     * BFS algorithm implementation - finds path with minimum stops
     */
    public Tree bfs(String start, String end) {
        int startIndex = getIndex(start);
        int endIndex = getIndex(end);
        
        if (startIndex == -1 || endIndex == -1) {
            return null;
        }
        
        List<Integer> searchOrder = new ArrayList<>();
        int[] parent = new int[vertices.size()];
        Arrays.fill(parent, -1);
        boolean[] visited = new boolean[vertices.size()];
        Queue<Integer> queue = new LinkedList<>();
        
        queue.offer(startIndex);
        visited[startIndex] = true;
        
        while (!queue.isEmpty()) {
            int u = queue.poll();
            searchOrder.add(u);
            
            if (u == endIndex) {
                return new Tree(startIndex, parent, searchOrder);
            }
            
            for (Edge e : neighbors.get(u)) {
                if (!visited[e.v]) {
                    visited[e.v] = true;
                    parent[e.v] = u;
                    queue.offer(e.v);
                }
            }
        }
        
        // If end was not reached
        return null;
    }
    
    /**
     * Get the path from the tree
     */
    public List<String> getPathFromTree(Tree tree, String start, String end) {
        if (tree == null) return null;
        
        int startIndex = getIndex(start);
        int endIndex = getIndex(end);
        if (startIndex == -1 || endIndex == -1) return null;
        
        List<String> path = new ArrayList<>();
        int current = endIndex;
        while (current != startIndex) {
            path.add(vertices.get(current));
            current = tree.parent[current];
            if (current == -1) {
                return null;
            }
        }
        path.add(vertices.get(startIndex));
        Collections.reverse(path);
        return path;
    }
    
    /**
     * Get the number of stops in a path
     */
    public int getStops(List<String> path) {
        return path != null ? path.size() - 1 : 0;
    }
    
    /**
     * Get the distance of a path using Dijkstra's graph data
     * Note: This assumes the Dijkstra class has a getTravelTime method
     */
    public double getPathDistance(List<String> path, Dijkstra<String> dijkstra) {
        if (path == null || path.size() < 2) return 0;
        double total = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            total += dijkstra.getTravelTime(path.get(i), path.get(i + 1));
        }
        return total;
    }
}