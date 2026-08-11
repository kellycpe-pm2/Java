import java.util.*;

class Dijkstra<V> {
    
    private List<V> vertices;
    private List<List<Edge>> neighbors;
    private Map<V, Map<V, Double>> travelTimes;
    
    public Dijkstra() {
        this.vertices = new ArrayList<>();
        this.neighbors = new ArrayList<>();
        this.travelTimes = new HashMap<>();
    }
    
    // ============================================
    // EDGE INNER CLASS
    // ============================================
    
    public static class Edge {
        public int u;
        public int v;
        
        public Edge(int u, int v) {
            this.u = u;
            this.v = v;
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Edge edge = (Edge) o;
            return u == edge.u && v == edge.v;
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(u, v);
        }
    }
    
    // ============================================
    // NODE DISTANCE - ADDED (was missing)
    // ============================================
    
    private static class NodeDistance implements Comparable<NodeDistance> {
        int index;
        double distance;
        
        NodeDistance(int index, double distance) {
            this.index = index;
            this.distance = distance;
        }
        
        @Override
        public int compareTo(NodeDistance other) {
            return Double.compare(this.distance, other.distance);
        }
    }
    
    // ============================================
    // TREE INNER CLASS
    // ============================================
    
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
        public int getParent(int v) { return parent[v]; }
        public List<Integer> getSearchOrder() { return searchOrder; }
        public int getNumberOfVerticesFound() { return searchOrder.size(); }
        
        public List<V> getPath(int index) {
            ArrayList<V> path = new ArrayList<>();
            int current = index;
            while (current != -1) {
                path.add(0, vertices.get(current));
                current = parent[current];
            }
            return path;
        }
    }
    
    // ============================================
    // GRAPH BUILDING METHODS
    // ============================================
    
    public boolean addVertex(V vertex) {
        if (!vertices.contains(vertex)) {
            vertices.add(vertex);
            neighbors.add(new ArrayList<>());
            travelTimes.put(vertex, new HashMap<>());
            return true;
        }
        return false;
    }
    
    public boolean addWeightedEdge(V from, V to, double weight) {
        int fromIndex = vertices.indexOf(from);
        int toIndex = vertices.indexOf(to);
        
        if (fromIndex == -1 || toIndex == -1) {
            return false;
        }
        
        neighbors.get(fromIndex).add(new Edge(fromIndex, toIndex));
        neighbors.get(toIndex).add(new Edge(toIndex, fromIndex));
        
        travelTimes.get(from).put(to, weight);
        travelTimes.get(to).put(from, weight);
        
        return true;
    }
    
    public int getSize() { return vertices.size(); }
    public List<V> getVertices() { return vertices; }
    public V getVertex(int index) { return vertices.get(index); }
    public int getIndex(V vertex) { return vertices.indexOf(vertex); }
    public List<Integer> getNeighbors(int index) {
        List<Integer> result = new ArrayList<>();
        for (Edge e : neighbors.get(index)) {
            result.add(e.v);
        }
        return result;
    }
    public int getDegree(int v) { return neighbors.get(v).size(); }
    
    public double getTravelTime(V from, V to) {
        if (travelTimes.containsKey(from) && travelTimes.get(from).containsKey(to)) {
            return travelTimes.get(from).get(to);
        }
        return Double.MAX_VALUE;
    }
    
    // ============================================
    // DIJKSTRA ALGORITHM
    // ============================================
    
    public Tree dijkstra(V start, V destination) {
        int startIndex = getIndex(start);
        if (startIndex == -1) return null;
        
        double[] distances = new double[vertices.size()];
        int[] parent = new int[vertices.size()];
        boolean[] isVisited = new boolean[vertices.size()];
        List<Integer> searchOrder = new ArrayList<>();
        
        for (int i = 0; i < distances.length; i++) {
            distances[i] = Double.MAX_VALUE;
            parent[i] = -1;
        }
        distances[startIndex] = 0;
        
        PriorityQueue<NodeDistance> pq = new PriorityQueue<>();
        pq.add(new NodeDistance(startIndex, 0));
        
        int destIndex = -1;
        if (destination != null) {
            destIndex = getIndex(destination);
            if (destIndex == -1) return null;
        }
        
        while (!pq.isEmpty()) {
            NodeDistance current = pq.poll();
            int u = current.index;
            
            if (isVisited[u]) continue;
            isVisited[u] = true;
            searchOrder.add(u);
            
            if (destination != null && u == destIndex) break;
            
            for (Edge e : neighbors.get(u)) {
                int v = e.v;
                if (isVisited[v]) continue;
                
                V fromVertex = vertices.get(u);
                V toVertex = vertices.get(v);
                double travelTime = getTravelTime(fromVertex, toVertex);
                
                if (travelTime == Double.MAX_VALUE) continue;
                
                double newDist = distances[u] + travelTime;
                if (newDist < distances[v]) {
                    distances[v] = newDist;
                    parent[v] = u;
                    pq.add(new NodeDistance(v, newDist));
                }
            }
        }
        
        return new Tree(startIndex, parent, searchOrder);
    }
    
    public Tree dijkstra(V start) {
        return dijkstra(start, null);
    }
    
    public List<V> getPathFromTree(Tree tree, V start, V destination) {
        if (tree == null) return null;
        
        int startIndex = getIndex(start);
        int destIndex = getIndex(destination);
        
        if (startIndex == -1 || destIndex == -1) return null;
        
        List<Integer> searchOrder = tree.getSearchOrder();
        if (!searchOrder.contains(destIndex)) return null;
        
        List<V> path = new ArrayList<>();
        int current = destIndex;
        while (current != startIndex && current != -1) {
            path.add(0, vertices.get(current));
            current = tree.getParent(current);
        }
        if (current == startIndex) {
            path.add(0, start);
        }
        return path;
    }
    
    public double getTotalDistance(List<V> path) {
        if (path == null || path.size() < 2) return 0;
        double total = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            total += getTravelTime(path.get(i), path.get(i + 1));
        }
        return total;
    }
    
    // ============================================
    // RESULT CLASS
    // ============================================
    
    public static class RouteResult<V> {
        private List<V> path;
        private double totalDistance;
        private int nodesExplored;
        private boolean found;
        private String algorithmName;
        
        public RouteResult(List<V> path, double totalDistance, int nodesExplored, 
                          boolean found, String algorithmName) {
            this.path = path;
            this.totalDistance = totalDistance;
            this.nodesExplored = nodesExplored;
            this.found = found;
            this.algorithmName = algorithmName;
        }
        
        public List<V> getPath() { return path; }
        public double getTotalDistance() { return totalDistance; }
        public int getNodesExplored() { return nodesExplored; }
        public boolean isFound() { return found; }
        public int getStops() { return path != null ? path.size() - 1 : 0; }
        public String getAlgorithmName() { return algorithmName; }
        
        @Override
        public String toString() {
            if (!found) {
                return "❌ No route found! (Explored " + nodesExplored + " nodes)";
            }
            StringBuilder sb = new StringBuilder();
            sb.append("Algorithm: ").append(algorithmName).append("\n");
            sb.append("Route: ");
            for (int i = 0; i < path.size(); i++) {
                sb.append(path.get(i));
                if (i < path.size() - 1) sb.append(" → ");
            }
            sb.append("\nTotal Distance: ").append(String.format("%.1f", totalDistance)).append(" KM");
            sb.append("\nStops: ").append(getStops());
            sb.append("\nNodes Explored: ").append(nodesExplored);
            return sb.toString();
        }
    }
    
    public RouteResult<V> findRoute(V start, V destination) {
        Tree tree = dijkstra(start, destination);
        if (tree == null) {
            return new RouteResult<>(null, 0, 0, false, "Dijkstra");
        }
        List<V> path = getPathFromTree(tree, start, destination);
        if (path == null || path.isEmpty()) {
            return new RouteResult<>(null, 0, tree.getNumberOfVerticesFound(), false, "Dijkstra");
        }
        double totalDistance = getTotalDistance(path);
        return new RouteResult<>(path, totalDistance, tree.getNumberOfVerticesFound(), true, "Dijkstra");
    }
}