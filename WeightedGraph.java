import java.util.*;

public class WeightedGraph<V> extends AbstractGraph<V> {
    
    /**
     * Construct an empty graph
     */
    public WeightedGraph() {
        super();
    }
    
    /**
     * Construct a graph from vertices and edges stored in arrays
     */
    public WeightedGraph(V[] vertices, int[][] edges) {
        super(vertices, edges);
    }
    


    /**
     * Construct a graph from integer vertices 0, 1, and edge array
     */
    public WeightedGraph(int[][] edges, int numberOfVertices) {
        super(edges, numberOfVertices);
    }
    
    /**
     * Add a weighted edge to the graph
     */
    public boolean addWeightedEdge(int u, int v, double weight) {
        return addEdge(new WeightedEdge(u, v, weight));
    }
    
    /**
     * Add a weighted edge using vertex objects
     */
    public boolean addWeightedEdge(V u, V v, double weight) {
        int indexU = getIndex(u);
        int indexV = getIndex(v);
        if (indexU == -1 || indexV == -1) return false;
        return addWeightedEdge(indexU, indexV, weight);
    }
    
    /**
     * Get the weight of an edge
     */
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
    
    /**
     * Get the weight of an edge between two vertices
     */
    public double getWeight(V u, V v) {
        int indexU = getIndex(u);
        int indexV = getIndex(v);
        if (indexU == -1 || indexV == -1) return Double.MAX_VALUE;
        return getWeight(indexU, indexV);
    }
    
    /**
     * Get all weighted edges
     */
    public List<WeightedEdge> getAllWeightedEdges() {
        List<WeightedEdge> result = new ArrayList<>();
        Set<String> added = new HashSet<>();
        
        for (int u = 0; u < neighbors.size(); u++) {
            for (Edge e : neighbors.get(u)) {
                if (e instanceof WeightedEdge) {
                    String key = Math.min(u, e.v) + "-" + Math.max(u, e.v);
                    if (!added.contains(key)) {
                        result.add((WeightedEdge) e);
                        added.add(key);
                    }
                }
            }
        }
        return result;
    }
    
    /**
     * Get total weight/distance of a path
     */
    public double getPathWeight(List<V> path) {
        if (path == null || path.size() < 2) return 0;
        double total = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            total += getWeight(path.get(i), path.get(i + 1));
        }
        return total;
    }
    
}