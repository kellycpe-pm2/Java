import java.util.*;

public class Astar<V> extends AbstractGraph<V> {
    
    private Map<String, Double> HeuristicMap = new HashMap<>();
    
    public Astar() {
        super();
        this.HeuristicMap = new HashMap<>();
    }
    
    public Astar(V[] vertices, int[][] edges) {
        super(vertices, edges);
        this.HeuristicMap = new HashMap<>();
    }

    public Astar(List<V> vertices, List<Edge> edges) {
        super(vertices, edges);
        this.HeuristicMap = new HashMap<>();
    }
    
    public Astar(List<Edge> edges, int numberOfVertices) {
        super(edges, numberOfVertices);
        this.HeuristicMap = new HashMap<>();
    }
    
    public Astar(int[][] edges, int numberOfVertices) {
        super(edges, numberOfVertices);
        this.HeuristicMap = new HashMap<>();
    }
    
    private String key(V u, V v) { return u + "->" + v; }

    public boolean addWeightedEdge(V u, V v, double weight) {
        int indexU = getIndex(u);
        int indexV = getIndex(v);
        if (indexU == -1 || indexV == -1) return false;
        return addEdge(indexU, indexV, weight);
    }
    
    public void addHeuristic(V from, V to, double val) {
        HeuristicMap.put(key(from, to) + "_h", val);
    }
    
    private double getHeuristic(V from, V to) {
        return HeuristicMap.getOrDefault(key(from, to) + "_h", 0.0);
    }
    
    public Tree solve(V start, V end) {
        int s = getIndex(start), e = getIndex(end);
        if (s == -1 || e == -1) return null;
        
        double[] g = new double[vertices.size()];
        int[] parent = new int[vertices.size()];
        boolean[] visited = new boolean[vertices.size()];
        Arrays.fill(g, Double.MAX_VALUE);
        Arrays.fill(parent, -1);
        g[s] = 0;
        
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingDouble(a -> a[1]));
        pq.add(new int[]{s, (int)getHeuristic(start, end)});
        
        while (!pq.isEmpty()) {
            int u = pq.poll()[0];
            if (visited[u]) continue;
            visited[u] = true;
            if (u == e) return new Tree(s, parent, new ArrayList<>());
            
            for (Edge edge : neighbors.get(u)) {
                int v = edge.v;
                if (!visited[v]) {
                    double ng = g[u] + getHeuristic(vertices.get(u), vertices.get(v));
                    if (ng < g[v]) {
                        g[v] = ng;
                        parent[v] = u;
                        pq.add(new int[]{v, (int)(ng + getHeuristic(vertices.get(v), end))});
                    }
                }
            }
        }
        return null;
    }
    
    public List<V> getPath(Tree tree, V start, V end) {
        if (tree == null) return null;
        List<V> path = new ArrayList<>();
        for (int i = getIndex(end); i != -1; i = tree.getParent(i)) {
            path.add(vertices.get(i));
            if (i == getIndex(start)) break;
        }
        Collections.reverse(path);
        return path.size() > 1 ? path : null;
    }
    
    public void printRoute(V start, V end) {
        Tree tree = solve(start, end);
        if (tree == null) { System.out.println("No route!"); return; }
        
        List<V> path = getPath(tree, start, end);
        System.out.println("\nRoute: " + String.join(" -> ", path.stream().map(Object::toString).toArray(String[]::new)));
        
        double dist = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            dist += getHeuristic(path.get(i), path.get(i + 1));
        }
        System.out.printf("Distance: %.1f KM | Stops: %d%n", dist, path.size() - 1);
    }
}
