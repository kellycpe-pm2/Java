import java.util.*;

public abstract class AbstractGraph<V> implements Graph<V> {

    public List<V> vertices = new ArrayList<>();
    public List<List<Edge>> neighbors = new ArrayList<>();

    public AbstractGraph() {
    }

    public AbstractGraph(V[] vertices, int[][] edges) {
        for (int i = 0; i < vertices.length; i++) {
            addVertex(vertices[i]);
        }
        createAdjacencyLists(edges, vertices.length);
    }

    public AbstractGraph(List<V> vertices, List<Edge> edges) {
        for (int i = 0; i < vertices.size(); i++) {
            addVertex(vertices.get(i));
        }
        createAdjacencyLists(edges, vertices.size());
    }


    public AbstractGraph(List<Edge> edges, int numberOfVertices) {
        for (int i = 0; i < numberOfVertices; i++) {
            addVertex((V) (new Integer(i)));
        }
        createAdjacencyLists(edges, numberOfVertices);
    }

    public AbstractGraph(int[][] edges, int numberOfVertices) {
        for (int i = 0; i < numberOfVertices; i++) {
            addVertex((V) (new Integer(i)));
        }
        createAdjacencyLists(edges, numberOfVertices);
    }

    private void createAdjacencyLists(int[][] edges, int numberOfVertices) {
        for (int i = 0; i < edges.length; i++) {
            addEdge(edges[i][0], edges[i][1]);
        }
    }

    private void createAdjacencyLists(List<Edge> edges, int numberOfVertices) {
        for (Edge edge : edges) {
            addEdge(edge.u, edge.v);
        }
    }


    @Override
    public int getSize() {
        return vertices.size();
    }

    @Override
    public List<V> getVertices() {
        return vertices;
    }

    @Override
    public V getVertex(int index) {
        return vertices.get(index);
    }

    @Override
    public int getIndex(V v) {
        return vertices.indexOf(v);
    }

    @Override
    public List<Integer> getNeighbors(int index) {
        List<Integer> result = new ArrayList<>();
        for (Edge e : neighbors.get(index)) {
            result.add(e.v);
        }
        return result;
    }

    @Override
    public int getDegree(int v) {
        return neighbors.get(v).size();
    }

    @Override
    public void printEdges() {
        for (int u = 0; u < neighbors.size(); u++) {
            System.out.print(getVertex(u) + " (" + u + "): ");
            for (Edge e : neighbors.get(u)) {
                System.out.print("(" + getVertex(e.u) + ", " + getVertex(e.v) + ") ");
            }
            System.out.println();
        }
    }

    @Override
    public void clear() {
        vertices.clear();
        neighbors.clear();
    }

    @Override
    public boolean addVertex(V vertex) {
        if (!vertices.contains(vertex)) {
            vertices.add(vertex);
            neighbors.add(new ArrayList<Edge>());
            return true;
        }
        return false;
    }

    public boolean addEdge(Edge e) {
        if (e.u < 0 || e.u > getSize() - 1) {
            throw new IllegalArgumentException("No such index: " + e.u);
        }
        if (e.v < 0 || e.v > getSize() - 1) {
            throw new IllegalArgumentException("No such index: " + e.v);
        }
        if (!neighbors.get(e.u).contains(e)) {
            neighbors.get(e.u).add(e);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean addEdge(int u, int v) {
        return addEdge(new Edge(u, v));
    }

    /**
     * Add a weighted edge
     */
    public boolean addEdge(int u, int v, double weight) {
        return addEdge(new WeightedEdge(u, v, weight));
    }

    /**
     * Get weight between two vertices
     */
    public double getWeight(V u, V v) {
        int indexU = getIndex(u);
        int indexV = getIndex(v);
        if (indexU == -1 || indexV == -1) return Double.MAX_VALUE;
        return getWeight(indexU, indexV);
    }

    /**
     * Get weight between two vertices by index
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
     * Get total distance of a path
     */
    public double getPathDistance(List<V> path) {
        if (path == null || path.size() < 2) return 0;
        double total = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            total += getWeight(path.get(i), path.get(i + 1));
        }
        return total;
    }

    /**
     * Get number of stops in a path
     */
    public int getStops(List<V> path) {
        return path != null ? path.size() - 1 : 0;
    }

    /**
     * Edge inner class
     */
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

    /**
     * WeightedEdge inner class
     */
    public static class WeightedEdge extends Edge {
        public double weight;

        public WeightedEdge(int u, int v, double weight) {
            super(u, v);
            this.weight = weight;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            WeightedEdge that = (WeightedEdge) o;
            return Double.compare(that.weight, weight) == 0;
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), weight);
        }
    }

    /**
     * Tree inner class
     */
    public class Tree {
        private int root;
        private int[] parent;
        private List<Integer> searchOrder;

        public Tree(int root, int[] parent, List<Integer> searchOrder) {
            this.root = root;
            this.parent = parent;
            this.searchOrder = searchOrder;
        }

        public int getRoot() {
            return root;
        }

        public int getParent(int v) {
            return parent[v];
        }

        public List<Integer> getSearchOrder() {
            return searchOrder;
        }

        public int getNumberOfVerticesFound() {
            return searchOrder.size();
        }

        public List<V> getPath(int index) {
            ArrayList<V> path = new ArrayList<>();
            int current = index;
            do {
                path.add(vertices.get(current));
                current = parent[current];
            } while (current != -1);
            return path;
        }

        public List<V> getPath(V start, V end) {
            int startIndex = getIndex(start);
            int endIndex = getIndex(end);
            if (startIndex == -1 || endIndex == -1) return null;
            return getPath(endIndex);
        }

        public void printPath(int index) {
            List<V> path = getPath(index);
            System.out.print("A path from " + vertices.get(root) + " to " + vertices.get(index) + ": ");
            for (int i = path.size() - 1; i >= 0; i--) {
                System.out.print(path.get(i) + " ");
            }
            System.out.println();
        }

        public void printPath(V start, V end) {
            List<V> path = getPath(start, end);
            if (path == null) {
                System.out.println("No path found!");
                return;
            }
            System.out.print("A path from " + start + " to " + end + ": ");
            for (int i = path.size() - 1; i >= 0; i--) {
                System.out.print(path.get(i) + " ");
            }
            System.out.println();
        }

        public void printTree() {
            System.out.println("Root is: " + vertices.get(root));
            System.out.print("Edges: ");
            for (int i = 0; i < parent.length; i++) {
                if (parent[i] != -1) {
                    System.out.print("(" + vertices.get(parent[i]) + ", " + vertices.get(i) + ") ");
                }
            }
            System.out.println();
        }
    }
}