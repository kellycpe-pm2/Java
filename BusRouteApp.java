import java.util.*;

public class BusRouteApp {
    
    // ============================================
    // GLOBAL VARIABLES
    // ============================================
    private static BFS bfs;
    private static Astar aStar;
    private static Dijkstra<String> dijkstra;
    private static String[] stations;
    private static Scanner scanner;
    
    // ============================================
    // RESULT CLASS
    // ============================================
    
    static class RouteResult {
        List<String> path;
        double totalDistance;
        int nodesExplored;
        String algorithmName;
        boolean found;
        int stops;
        
        RouteResult(List<String> path, double totalDistance, int nodesExplored, 
                   String algorithmName, boolean found) {
            this.path = path;
            this.totalDistance = totalDistance;
            this.nodesExplored = nodesExplored;
            this.algorithmName = algorithmName;
            this.found = found;
            this.stops = path != null ? path.size() - 1 : 0;
        }
        
        int getStops() { return stops; }
        
        @Override
        public String toString() {
            if (!found) {
                return "❌ No path found! (Explored " + nodesExplored + " nodes)";
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
    
    // ============================================
    // MAIN METHOD
    // ============================================
    
    public static void main(String[] args) {
        scanner = new Scanner(System.in);
        
        buildNetwork();
        runMenu();
        
        scanner.close();
    }
    
    // ============================================
    // BUILD NETWORK
    // ============================================
    
    private static void buildNetwork() {
        System.out.println("\n🏗️  BUILDING BUS NETWORK...");
        System.out.println("-".repeat(50));
        
        // Initialize algorithms
        dijkstra = new Dijkstra<>();
        bfs = new BFS();
        
        stations = new String[]{
            "Batu Ferringhi", "Tanjung Bungah", "Straits Quay", "Pulau Tikus",
            "Gurney Drive", "Komtar", "Weld Quay", "Sungai Pinang",
            "Jelutong", "USM Gelugor"
        };
        
        // Add vertices to all algorithms
        for (String station : stations) {
            dijkstra.addVertex(station);
            bfs.addVertex(station);
        }
        
        // Add routes
        String[][] routes = {
            {"Batu Ferringhi", "Tanjung Bungah", "5.7"},
            {"Batu Ferringhi", "Pulau Tikus", "13.0"},
            {"Tanjung Bungah", "Pulau Tikus", "7.5"},
            {"Tanjung Bungah", "Straits Quay", "4.3"},
            {"Straits Quay", "Pulau Tikus", "5.9"},
            {"Straits Quay", "Gurney Drive", "4.4"},
            {"Straits Quay", "Komtar", "7.9"},
            {"Pulau Tikus", "Komtar", "4.4"},
            {"Gurney Drive", "Komtar", "3.5"},
            {"Gurney Drive", "Weld Quay", "4.9"},
            {"Komtar", "Weld Quay", "1.5"},
            {"Komtar", "USM Gelugor", "9.0"},
            {"Sungai Pinang", "Jelutong", "2.8"},
            {"Jelutong", "USM Gelugor", "5.8"},
            {"Komtar", "Sungai Pinang", "2.4"}
        };
        
        for (String[] route : routes) {
            double distance = Double.parseDouble(route[2]);
            dijkstra.addWeightedEdge(route[0], route[1], distance);
            bfs.addEdge(route[0], route[1], distance);
        }
                
        System.out.println("✅ Network built successfully!");
        System.out.printf("   - %d bus stops%n", stations.length);
        System.out.printf("   - %d bidirectional routes%n", routes.length);
        System.out.println("   - Algorithms: Dijkstra, A*, BFS");
    }
    
    // ============================================
    // ADD HEURISTICS FOR A*
    // ============================================
    
    // ============================================
    // MENU SYSTEM
    // ============================================
    
    private static void runMenu() {
        while (true) {
            System.out.println("\n" + "=".repeat(80));
            System.out.println("📋 MAIN MENU");
            System.out.println("=".repeat(80));
            System.out.println("  1. 🔍 Find Route (Dijkstra)");
            System.out.println("  2. 🔍 Find Route (A*)");
            System.out.println("  3. 🔍 Find Route (BFS)");
            System.out.println("  4. 🗺️  Show All Routes");
            System.out.println("  5. 📊 Show Network Details");
            System.out.println("  6. 🚪 Exit");
            System.out.println("-".repeat(80));
            
            System.out.print("\nEnter your choice (1-6): ");
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    findRouteDijkstra();
                    break;
                case "2":
                    break;
                case "3":
                    findRouteBFS();
                    break;
                case "4":
                    showAllRoutes();
                    break;
                case "5":
                    showNetworkDetails();
                    break;
                case "6":
                    System.out.println(" Exiting the application. Goodbye!");
                    return;
                default:
                    System.out.println(" Invalid choice!");
            }
        }
    }
    
    // ============================================
    // GET USER INPUT
    // ============================================
    
    private static String[] getUserRouteInput() {
        System.out.println("\n📍 Available Stations:");
        System.out.println("-".repeat(50));
        for (int i = 0; i < stations.length; i++) {
            System.out.printf("  %2d. %s%n", i + 1, stations[i]);
        }
        
        try {
            System.out.print("\nEnter START station number (1-" + stations.length + "): ");
            int startNum = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Enter DESTINATION station number (1-" + stations.length + "): ");
            int destNum = Integer.parseInt(scanner.nextLine().trim());
            
            if (startNum < 1 || startNum > stations.length || destNum < 1 || destNum > stations.length) {
                System.out.println("❌ Invalid station number!");
                return null;
            }
            
            String start = stations[startNum - 1];
            String dest = stations[destNum - 1];
            
            if (start.equals(dest)) {
                System.out.println(" Start and destination are the same!");
                return null;
            }
            
            return new String[]{start, dest};
            
        } catch (NumberFormatException e) {
            System.out.println(" Please enter a valid number!");
            return null;
        }
    }
    
    // ============================================
    // DIJKSTRA
    // ============================================
    
    private static void findRouteDijkstra() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("🔍 DIJKSTRA - SHORTEST PATH");
        System.out.println("=".repeat(80));
        System.out.println("\n📌 Finds path with MINIMUM TOTAL DISTANCE");
        
        String[] input = getUserRouteInput();
        if (input == null) return;
        
        String start = input[0];
        String dest = input[1];
        
        long startTime = System.nanoTime();
        Dijkstra<String>.Tree tree = dijkstra.dijkstra(start, dest);
        long endTime = System.nanoTime();
        double timeMs = (endTime - startTime) / 1_000_000.0;
        
        if (tree == null) {
            System.out.println("❌ No path found!");
            return;
        }
        
        List<String> path = dijkstra.getPathFromTree(tree, start, dest);
        double totalDistance = dijkstra.getTotalDistance(path);
        int nodesExplored = tree.getNumberOfVerticesFound();
        
        RouteResult result = new RouteResult(path, totalDistance, nodesExplored, "Dijkstra", path != null);
        displayResult(result, start, dest, timeMs);
    }
    
    
    // ============================================
    // BFS
    // ============================================
    
    private static void findRouteBFS() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("🔍 BFS - MINIMUM STOPS");
        System.out.println("=".repeat(80));
        System.out.println("\n📌 Finds path with MINIMUM NUMBER OF BUS CHANGES/STOPS");
        
        String[] input = getUserRouteInput();
        if (input == null) return;
        
        String start = input[0];
        String dest = input[1];
        
        long startTime = System.nanoTime();
        BFS.Tree tree = bfs.bfs(start, dest);
        long endTime = System.nanoTime();
        double timeMs = (endTime - startTime) / 1_000_000.0;
        
        if (tree == null) {
            System.out.println("❌ No path found!");
            return;
        }
        
        List<String> path = bfs.getPathFromTree(tree, start, dest);
        int stops = bfs.getStops(path);
        int nodesExplored = tree.getNumberOfVerticesFound();
        
        // Calculate distance for BFS path
        double totalDistance = 0;
        if (path != null) {
            for (int i = 0; i < path.size() - 1; i++) {
                totalDistance += dijkstra.getTravelTime(path.get(i), path.get(i + 1));
            }
        }
        
        RouteResult result = new RouteResult(path, totalDistance, nodesExplored, "BFS", path != null);
        result.stops = stops;
        displayResult(result, start, dest, timeMs);
    }
    
    // ============================================
    // DISPLAY RESULT
    // ============================================
    
    private static void displayResult(RouteResult result, String start, 
                                      String destination, double time) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("✅ " + result.algorithmName + " RESULT");
        System.out.println("=".repeat(80));
        
        if (!result.found || result.path == null || result.path.isEmpty()) {
            System.out.println("❌ No route found between " + start + " and " + destination);
            return;
        }
        
        System.out.println("\n📍 Route: " + start + " → " + destination);
        System.out.println("-".repeat(60));
        System.out.println("Path: " + String.join(" → ", result.path));
        
        System.out.println("\n📏 Route Details:");
        System.out.println("-".repeat(60));
        
        for (int i = 0; i < result.path.size() - 1; i++) {
            String from = result.path.get(i);
            String to = result.path.get(i + 1);
            double dist = dijkstra.getTravelTime(from, to);
            System.out.printf("  %d. %-20s → %-20s (%.1f KM)%n", 
                i + 1, from, to, dist);
        }
        
        System.out.println("-".repeat(60));
        System.out.printf("Total Distance: %.1f KM%n", result.totalDistance);
        System.out.printf("Number of Stops: %d%n", result.getStops());
        System.out.printf("Nodes Explored: %d%n", result.nodesExplored);
        System.out.printf("Execution Time: %.3f ms%n", time);
        System.out.println("=".repeat(80));
    }
    
    // ============================================
    // SHOW ALL ROUTES
    // ============================================
    
    private static void showAllRoutes() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("🗺️  ALL BUS ROUTES");
        System.out.println("=".repeat(80));
        
        System.out.println("\n📋 Complete Bus Network (with distances in KM):");
        System.out.println("-".repeat(60));
        
        for (int i = 0; i < stations.length; i++) {
            String from = stations[i];
            System.out.printf("%-20s → ", from);
            List<String> connections = new ArrayList<>();
            for (int j = 0; j < stations.length; j++) {
                if (j > i) {
                    String to = stations[j];
                    double dist = dijkstra.getTravelTime(from, to);
                    if (dist < Double.MAX_VALUE) {
                        connections.add(to + " (" + String.format("%.1f", dist) + " KM)");
                    }
                }
            }
            System.out.println(String.join(", ", connections));
        }
    }
    
    // ============================================
    // SHOW NETWORK DETAILS
    // ============================================
    
    private static void showNetworkDetails() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("📊 NETWORK DETAILS");
        System.out.println("=".repeat(80));
        
        System.out.println("\n📈 DEGREE ANALYSIS:");
        System.out.println("-".repeat(50));
        int maxDegree = 0;
        String mostConnected = "";
        
        for (int i = 0; i < stations.length; i++) {
            String station = stations[i];
            int degree = 0;
            for (int j = 0; j < stations.length; j++) {
                if (i != j && dijkstra.getTravelTime(station, stations[j]) < Double.MAX_VALUE) {
                    degree++;
                }
            }
            String bar = "█".repeat(Math.min(degree, 10));
            System.out.printf("  %-20s : Degree %2d %s%n", station, degree, bar);
            
            if (degree > maxDegree) {
                maxDegree = degree;
                mostConnected = station;
            }
        }
        
        System.out.println("\n🏆 MOST CONNECTED: " + mostConnected + " (Degree: " + maxDegree + ")");
        
        System.out.println("\n📋 DISTANCE MATRIX (KM):");
        System.out.println("-".repeat(90));
        System.out.print("       ");
        for (String station : stations) {
            System.out.printf("%-8s", station.substring(0, Math.min(6, station.length())));
        }
        System.out.println();
        System.out.println("-".repeat(90));
        
        for (String row : stations) {
            System.out.printf("%-7s", row.substring(0, Math.min(6, row.length())));
            for (String col : stations) {
                if (row.equals(col)) {
                    System.out.printf("%-8s", "●");
                } else {
                    double dist = dijkstra.getTravelTime(row, col);
                    if (dist < Double.MAX_VALUE) {
                        System.out.printf("%-8.1f", dist);
                    } else {
                        System.out.printf("%-8s", "✗");
                    }
                }
            }
            System.out.println();
        }
        System.out.println("=".repeat(80));
    }
}