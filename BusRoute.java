import java.util.*;
import java.io.*;

public class BusRoute {
    
    private static BFS<String> bfs;
    private static DFS<String> dfs;
    private static Dijkstra<String> dijkstra;
    private static astar1<String> astar1;
    private static List<String> stations;
    private static Scanner scanner;
    private static final String DATA_FILE = "bus_routes.txt";
    
    public static void main(String[] args) {
        scanner = new Scanner(System.in);
        stations = new ArrayList<>();
        
        printHeader();
        
        // Try to load from file first
        if (!loadFromFile()) {
            buildDefaultNetwork();
        }
        
        while (true) {
            printMenu();
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    findRouteBFS();
                    break;
                case "2":
                    findRouteDFS();
                    break;
                case "3":
                    findRouteDijkstra();
                    break;
                case "4":
                    findRouteastar1();
                    break;
                case "5":
                    showAllRoutes();
                    break;
                case "6":
                    showNetworkDetails();
                    break;
                case "7":
                    addStation();
                    break;
                case "8":
                    removeStation();
                    break;
                case "9":
                    addRoute();
                    break;
                case "10":
                    removeRoute();
                    break;
                case "11":
                    saveToFile();
                    break;
                case "12":
                    System.out.println("\nExiting the application. Goodbye!");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice! Please enter 1-12.");
            }
        }
    }
    
    private static void printHeader() {
        System.out.println("=".repeat(80));
        System.out.println("  BUS ROUTE MANAGEMENT SYSTEM");
        System.out.println("  TUNKU ABDUL RAHMAN UNIVERSITY");
        System.out.println("  AMCS2034 - DATA STRUCTURE & ALGORITHMS");
        System.out.println("=".repeat(80));
        System.out.println("  Algorithms: BFS, DFS, Dijkstra, A*");
        System.out.println("=".repeat(80));
    }
    
    private static void printMenu() {
        System.out.println("\nMAIN MENU");
        System.out.println("-".repeat(80));
        System.out.println("  FIND ROUTES:");
        System.out.println("  1. Find Route (BFS - Minimum Stops)");
        System.out.println("  2. Find Route (DFS - Depth First Search)");
        System.out.println("  3. Find Route (Dijkstra - Shortest Distance)");
        System.out.println("  4. Find Route (A* - Shortest with Heuristic)");
        System.out.println("-".repeat(80));
        System.out.println("  VIEW:");
        System.out.println("  5. Show All Routes");
        System.out.println("  6. Show Network Details");
        System.out.println("-".repeat(80));
        System.out.println("  EDIT NETWORK:");
        System.out.println("  7. Add Station");
        System.out.println("  8. Remove Station");
        System.out.println("  9. Add Route");
        System.out.println("  10. Remove Route");
        System.out.println("-".repeat(80));
        System.out.println("  FILE OPERATIONS:");
        System.out.println("  11. Save to File");
        System.out.println("  12. Exit");
        System.out.println("-".repeat(80));
        System.out.print("Enter your choice (1-12): ");
    }
    
    private static void initializeAlgorithms() {
        bfs = new BFS<>();
        dfs = new DFS<>();
        dijkstra = new Dijkstra<>();
        astar1 = new astar1<>();
        
        // Add all stations to algorithms
        for (String station : stations) {
            bfs.addVertex(station);
            dfs.addVertex(station);
            dijkstra.addVertex(station);
            astar1.addVertex(station);
        }
    }
    
    private static void buildDefaultNetwork() {
        System.out.println("\nBuilding Default Bus Network...");
        System.out.println("-".repeat(50));
        
        stations = new ArrayList<>(Arrays.asList(
            "Batu Ferringhi", "Tanjung Bungah", "Straits Quay", "Pulau Tikus",
            "Gurney Drive", "Komtar", "Weld Quay", "Sungai Pinang",
            "Jelutong", "USM Gelugor"
        ));
        
        initializeAlgorithms();
        
        // Add routes with distances
        addRouteToAlgorithms("Batu Ferringhi", "Tanjung Bungah", 5.7);
        addRouteToAlgorithms("Batu Ferringhi", "Pulau Tikus", 13.0);
        addRouteToAlgorithms("Tanjung Bungah", "Pulau Tikus", 7.5);
        addRouteToAlgorithms("Tanjung Bungah", "Straits Quay", 4.3);
        addRouteToAlgorithms("Straits Quay", "Pulau Tikus", 5.9);
        addRouteToAlgorithms("Straits Quay", "Gurney Drive", 4.4);
        addRouteToAlgorithms("Straits Quay", "Komtar", 7.9);
        addRouteToAlgorithms("Pulau Tikus", "Komtar", 4.4);
        addRouteToAlgorithms("Gurney Drive", "Komtar", 3.5);
        addRouteToAlgorithms("Gurney Drive", "Weld Quay", 4.9);
        addRouteToAlgorithms("Komtar", "Weld Quay", 1.5);
        addRouteToAlgorithms("Komtar", "USM Gelugor", 9.0);
        addRouteToAlgorithms("Sungai Pinang", "Jelutong", 2.8);
        addRouteToAlgorithms("Jelutong", "USM Gelugor", 5.8);
        addRouteToAlgorithms("Komtar", "Sungai Pinang", 2.4);
        
        // Add heuristics for A*
        addHeuristics();
        
        System.out.println("Default network built successfully!");
        System.out.println("   - " + stations.size() + " bus stops");
        System.out.println("   - 15 bus routes");
        System.out.println("   - Algorithms: BFS, DFS, Dijkstra, A*");
    }
    
    private static void addRouteToAlgorithms(String station1, String station2, double distance) {
        bfs.addWeightedEdge(station1, station2, distance);
        dfs.addWeightedEdge(station1, station2, distance);
        dijkstra.addWeightedEdge(station1, station2, distance);
        astar1.addWeightedEdge(station1, station2, distance);
    }
    
    private static void addHeuristics() {
        astar1.addHeuristic("Batu Ferringhi", "Tanjung Bungah", 5.7);
        astar1.addHeuristic("Batu Ferringhi", "Pulau Tikus", 13.0);
        astar1.addHeuristic("Batu Ferringhi", "Komtar", 15.0);
        astar1.addHeuristic("Tanjung Bungah", "Straits Quay", 4.3);
        astar1.addHeuristic("Tanjung Bungah", "Komtar", 9.0);
        astar1.addHeuristic("Straits Quay", "Komtar", 7.9);
        astar1.addHeuristic("Pulau Tikus", "Komtar", 4.4);
        astar1.addHeuristic("Gurney Drive", "Komtar", 3.5);
        astar1.addHeuristic("Komtar", "Weld Quay", 1.5);
        astar1.addHeuristic("Komtar", "USM Gelugor", 9.0);
        astar1.addHeuristic("Sungai Pinang", "Jelutong", 2.8);
        astar1.addHeuristic("Jelutong", "USM Gelugor", 5.8);
    }
    
    // ============================================
    // ADD STATION
    // ============================================
    
    private static void addStation() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("ADD NEW STATION");
        System.out.println("=".repeat(80));
        
        System.out.print("\nEnter new station name: ");
        String newStation = scanner.nextLine().trim();
        
        if (newStation.isEmpty()) {
            System.out.println("Station name cannot be empty!");
            return;
        }
        
        if (stations.contains(newStation)) {
            System.out.println("Station '" + newStation + "' already exists!");
            return;
        }
        
        // Add to all algorithms
        bfs.addVertex(newStation);
        dfs.addVertex(newStation);
        dijkstra.addVertex(newStation);
        astar1.addVertex(newStation);
        stations.add(newStation);
        
        System.out.println("Station '" + newStation + "' added successfully!");
        System.out.println("Total stations: " + stations.size());
    }
    
    // ============================================
    // REMOVE STATION
    // ============================================
    
    private static void removeStation() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("REMOVE STATION");
        System.out.println("=".repeat(80));
        
        if (stations.isEmpty()) {
            System.out.println("No stations available to remove!");
            return;
        }
        
        displayStations();
        System.out.print("\nEnter station number to remove: ");
        
        try {
            int index = Integer.parseInt(scanner.nextLine().trim()) - 1;
            
            if (index < 0 || index >= stations.size()) {
                System.out.println("Invalid station number!");
                return;
            }
            
            String stationToRemove = stations.get(index);
            
            // Check if station has connections
            int degree = 0;
            for (int i = 0; i < stations.size(); i++) {
                if (i != index && dijkstra.getWeight(stationToRemove, stations.get(i)) < Double.MAX_VALUE) {
                    degree++;
                }
            }
            
            if (degree > 0) {
                System.out.println("Station '" + stationToRemove + "' has " + degree + " connections.");
                System.out.print("Are you sure you want to remove it? (y/n): ");
                String confirm = scanner.nextLine().trim().toLowerCase();
                if (!confirm.equals("y")) {
                    System.out.println("Removal cancelled.");
                    return;
                }
            }
            
            // Remove from all algorithms (need to rebuild graph)
            rebuildGraphWithoutStation(index);
            
            stations.remove(index);
            System.out.println("Station '" + stationToRemove + "' removed successfully!");
            System.out.println("Total stations: " + stations.size());
            
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number!");
        }
    }
    
    private static void rebuildGraphWithoutStation(int removeIndex) {
        // Rebuild all algorithms without the removed station
        bfs = new BFS<>();
        dfs = new DFS<>();
        dijkstra = new Dijkstra<>();
        astar1 = new astar1<>();
        
        // Add all remaining stations
        for (int i = 0; i < stations.size(); i++) {
            if (i != removeIndex) {
                String station = stations.get(i);
                bfs.addVertex(station);
                dfs.addVertex(station);
                dijkstra.addVertex(station);
                astar1.addVertex(station);
            }
        }
        
        // Re-add all routes (skipping removed station)
        for (int i = 0; i < stations.size(); i++) {
            if (i == removeIndex) continue;
            for (int j = i + 1; j < stations.size(); j++) {
                if (j == removeIndex) continue;
                double dist = dijkstra.getWeight(stations.get(i), stations.get(j));
                if (dist < Double.MAX_VALUE) {
                    addRouteToAlgorithms(stations.get(i), stations.get(j), dist);
                }
            }
        }
    }
    
    // ============================================
    // ADD ROUTE
    // ============================================
    
    private static void addRoute() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("ADD NEW ROUTE");
        System.out.println("=".repeat(80));
        
        if (stations.size() < 2) {
            System.out.println("Need at least 2 stations to add a route!");
            return;
        }
        
        displayStations();
        
        try {
            System.out.print("\nEnter FROM station number: ");
            int fromIndex = Integer.parseInt(scanner.nextLine().trim()) - 1;
            System.out.print("Enter TO station number: ");
            int toIndex = Integer.parseInt(scanner.nextLine().trim()) - 1;
            
            if (fromIndex < 0 || fromIndex >= stations.size() || toIndex < 0 || toIndex >= stations.size()) {
                System.out.println("Invalid station number!");
                return;
            }
            
            if (fromIndex == toIndex) {
                System.out.println("Cannot add route to the same station!");
                return;
            }
            
            String from = stations.get(fromIndex);
            String to = stations.get(toIndex);
            
            // Check if route already exists
            if (dijkstra.getWeight(from, to) < Double.MAX_VALUE) {
                System.out.println("Route between '" + from + "' and '" + to + "' already exists!");
                return;
            }
            
            System.out.print("Enter distance in KM: ");
            double distance = Double.parseDouble(scanner.nextLine().trim());
            
            if (distance <= 0) {
                System.out.println("Distance must be positive!");
                return;
            }
            
            // Add route to all algorithms
            addRouteToAlgorithms(from, to, distance);
            
            // Add heuristic for A*
            astar1.addHeuristic(from, to, distance);
            
            System.out.println("Route added successfully!");
            System.out.println("  " + from + " <-> " + to + " (" + distance + " KM)");
            
        } catch (NumberFormatException e) {
            System.out.println("Please enter valid numbers!");
        }
    }
    
    // ============================================
    // REMOVE ROUTE
    // ============================================
    
    private static void removeRoute() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("REMOVE ROUTE");
        System.out.println("=".repeat(80));
        
        if (stations.size() < 2) {
            System.out.println("Not enough stations to remove a route!");
            return;
        }
        
        displayStations();
        
        try {
            System.out.print("\nEnter FROM station number: ");
            int fromIndex = Integer.parseInt(scanner.nextLine().trim()) - 1;
            System.out.print("Enter TO station number: ");
            int toIndex = Integer.parseInt(scanner.nextLine().trim()) - 1;
            
            if (fromIndex < 0 || fromIndex >= stations.size() || toIndex < 0 || toIndex >= stations.size()) {
                System.out.println("Invalid station number!");
                return;
            }
            
            if (fromIndex == toIndex) {
                System.out.println("Cannot remove route to the same station!");
                return;
            }
            
            String from = stations.get(fromIndex);
            String to = stations.get(toIndex);
            
            // Check if route exists
            double currentDist = dijkstra.getWeight(from, to);
            if (currentDist >= Double.MAX_VALUE) {
                System.out.println("No route exists between '" + from + "' and '" + to + "'!");
                return;
            }
            
            System.out.println("Current route: " + from + " <-> " + to + " (" + currentDist + " KM)");
            System.out.print("Are you sure you want to remove this route? (y/n): ");
            String confirm = scanner.nextLine().trim().toLowerCase();
            
            if (!confirm.equals("y")) {
                System.out.println("Removal cancelled.");
                return;
            }
            
            // Remove route by rebuilding graph without this connection
            rebuildGraphWithoutRoute(fromIndex, toIndex);
            
            System.out.println("Route removed successfully!");
            
        } catch (NumberFormatException e) {
            System.out.println("Please enter valid numbers!");
        }
    }
    
    private static void rebuildGraphWithoutRoute(int fromIdx, int toIdx) {
        // Rebuild all algorithms without the specific route
        bfs = new BFS<>();
        dfs = new DFS<>();
        dijkstra = new Dijkstra<>();
        astar1 = new astar1<>();
        
        // Add all stations
        for (String station : stations) {
            bfs.addVertex(station);
            dfs.addVertex(station);
            dijkstra.addVertex(station);
            astar1.addVertex(station);
        }
        
        // Add all routes except the one to remove
        for (int i = 0; i < stations.size(); i++) {
            for (int j = i + 1; j < stations.size(); j++) {
                if (i == fromIdx && j == toIdx) continue;
                if (i == toIdx && j == fromIdx) continue;
                
                double dist = dijkstra.getWeight(stations.get(i), stations.get(j));
                if (dist < Double.MAX_VALUE) {
                    addRouteToAlgorithms(stations.get(i), stations.get(j), dist);
                }
            }
        }
    }
    
    // ============================================
    // DISPLAY STATIONS
    // ============================================
    
    private static void displayStations() {
        System.out.println("\nStations:");
        System.out.println("-".repeat(50));
        for (int i = 0; i < stations.size(); i++) {
            System.out.printf("  %2d. %s%n", i + 1, stations.get(i));
        }
    }
    
    // ============================================
    // SAVE TO FILE
    // ============================================
    
    private static void saveToFile() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("SAVE TO FILE");
        System.out.println("=".repeat(80));
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(DATA_FILE))) {
            // Write number of stations
            writer.println(stations.size());
            
            // Write all station names
            for (String station : stations) {
                writer.println(station);
            }
            
            // Write all routes
            int routeCount = 0;
            for (int i = 0; i < stations.size(); i++) {
                for (int j = i + 1; j < stations.size(); j++) {
                    double dist = dijkstra.getWeight(stations.get(i), stations.get(j));
                    if (dist < Double.MAX_VALUE) {
                        writer.println(stations.get(i) + "," + stations.get(j) + "," + dist);
                        routeCount++;
                    }
                }
            }
            
            System.out.println("Network saved successfully to '" + DATA_FILE + "'!");
            System.out.println("   - " + stations.size() + " stations");
            System.out.println("   - " + routeCount + " routes");
            
        } catch (IOException e) {
            System.out.println("Error saving to file: " + e.getMessage());
        }
    }
    
    // ============================================
    // LOAD FROM FILE
    // ============================================
    
    private static boolean loadFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))) {
            System.out.println("\nLoading network from file...");
            
            // Read number of stations
            int numStations = Integer.parseInt(reader.readLine().trim());
            
            // Read station names
            stations.clear();
            for (int i = 0; i < numStations; i++) {
                stations.add(reader.readLine().trim());
            }
            
            initializeAlgorithms();
            
            // Read routes
            String line;
            int routeCount = 0;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String from = parts[0].trim();
                    String to = parts[1].trim();
                    double dist = Double.parseDouble(parts[2].trim());
                    addRouteToAlgorithms(from, to, dist);
                    routeCount++;
                }
            }
            
            System.out.println("Network loaded successfully!");
            System.out.println("   - " + stations.size() + " stations");
            System.out.println("   - " + routeCount + " routes");
            return true;
            
        } catch (FileNotFoundException e) {
            System.out.println("\nNo saved network found. Building default network...");
            return false;
        } catch (IOException | NumberFormatException e) {
            System.out.println("\nError loading file. Building default network...");
            return false;
        }
    }
    
    // ============================================
    // ROUTE FINDING METHODS
    // ============================================
    
    private static String[] getUserRouteInput() {
        displayStations();
        
        try {
            System.out.print("\nEnter START station number (1-" + stations.size() + "): ");
            int startNum = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Enter DESTINATION station number (1-" + stations.size() + "): ");
            int destNum = Integer.parseInt(scanner.nextLine().trim());
            
            if (startNum < 1 || startNum > stations.size() || destNum < 1 || destNum > stations.size()) {
                System.out.println("Invalid station number!");
                return null;
            }
            
            String start = stations.get(startNum - 1);
            String dest = stations.get(destNum - 1);
            
            if (start.equals(dest)) {
                System.out.println("Start and destination are the same!");
                return null;
            }
            
            return new String[]{start, dest};
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number!");
            return null;
        }
    }
    
    private static void findRouteBFS() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("BFS - MINIMUM STOPS ROUTE");
        System.out.println("=".repeat(80));
        System.out.println("Finds path with MINIMUM NUMBER OF BUS STOPS");
        
        String[] input = getUserRouteInput();
        if (input == null) return;
        
        String start = input[0];
        String dest = input[1];
        
        long startTime = System.nanoTime();
        AbstractGraph<String>.Tree tree = bfs.bfs(start, dest);
        long endTime = System.nanoTime();
        double timeMs = (endTime - startTime) / 1_000_000.0;
        
        if (tree == null) {
            System.out.println("No path found between " + start + " and " + dest);
            return;
        }
        
        List<String> path = bfs.getPathFromTree(tree, start, dest);
        int stops = bfs.getStops(path);
        double totalDistance = bfs.getPathDistance(path);
        int nodesExplored = tree.getNumberOfVerticesFound();
        
        printResult("BFS", start, dest, path, stops, totalDistance, nodesExplored, timeMs);
    }
    
    private static void findRouteDFS() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("DFS - DEPTH FIRST SEARCH");
        System.out.println("=".repeat(80));
        System.out.println("Finds path by exploring depth-first (may not be optimal)");
        
        String[] input = getUserRouteInput();
        if (input == null) return;
        
        String start = input[0];
        String dest = input[1];
        
        long startTime = System.nanoTime();
        AbstractGraph<String>.Tree tree = dfs.dfsRecursive(start, dest);
        long endTime = System.nanoTime();
        double timeMs = (endTime - startTime) / 1_000_000.0;
        
        if (tree == null) {
            System.out.println("No path found between " + start + " and " + dest);
            return;
        }
        
        List<String> path = dfs.getPathFromTree(tree, start, dest);
        int stops = dfs.getStops(path);
        double totalDistance = dfs.getPathDistance(path);
        int nodesExplored = tree.getNumberOfVerticesFound();
        
        printResult("DFS", start, dest, path, stops, totalDistance, nodesExplored, timeMs);
    }
    
    private static void findRouteDijkstra() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("DIJKSTRA - SHORTEST DISTANCE ROUTE");
        System.out.println("=".repeat(80));
        System.out.println("Finds path with MINIMUM TOTAL DISTANCE");
        
        String[] input = getUserRouteInput();
        if (input == null) return;
        
        String start = input[0];
        String dest = input[1];
        
        long startTime = System.nanoTime();
        AbstractGraph<String>.Tree tree = dijkstra.dijkstra(start, dest);
        long endTime = System.nanoTime();
        double timeMs = (endTime - startTime) / 1_000_000.0;
        
        if (tree == null) {
            System.out.println("No path found between " + start + " and " + dest);
            return;
        }
        
        List<String> path = dijkstra.getPathFromTree(tree, start, dest);
        int stops = dijkstra.getStops(path);
        double totalDistance = dijkstra.getPathDistance(path);
        int nodesExplored = tree.getNumberOfVerticesFound();
        
        printResult("Dijkstra", start, dest, path, stops, totalDistance, nodesExplored, timeMs);
    }
    
    private static void findRouteastar1() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("A* - SHORTEST DISTANCE WITH HEURISTIC");
        System.out.println("=".repeat(80));
        System.out.println("Finds path with MINIMUM TOTAL DISTANCE (Faster with heuristic)");
        
        String[] input = getUserRouteInput();
        if (input == null) return;
        
        String start = input[0];
        String dest = input[1];
        
        long startTime = System.nanoTime();
        AbstractGraph<String>.Tree tree = astar1.astar1(start, dest);
        long endTime = System.nanoTime();
        double timeMs = (endTime - startTime) / 1_000_000.0;
        
        if (tree == null) {
            System.out.println("No path found between " + start + " and " + dest);
            return;
        }
        
        List<String> path = astar1.getPathFromTree(tree, start, dest);
        int stops = astar1.getStops(path);
        double totalDistance = astar1.getPathDistance(path);
        int nodesExplored = tree.getNumberOfVerticesFound();
        
        printResult("A*", start, dest, path, stops, totalDistance, nodesExplored, timeMs);
    }
    
    private static void printResult(String algorithm, String start, String dest,
                                    List<String> path, int stops, double distance,
                                    int nodesExplored, double timeMs) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println(algorithm + " RESULT");
        System.out.println("=".repeat(80));
        
        System.out.println("\nRoute: " + start + " -> " + dest);
        System.out.println("-".repeat(60));
        System.out.println("Path: " + String.join(" -> ", path));
        
        System.out.println("\nRoute Details:");
        System.out.println("-".repeat(60));
        
        double totalDistance = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            String from = path.get(i);
            String to = path.get(i + 1);
            double dist = dijkstra.getWeight(from, to);
            totalDistance += dist;
            System.out.printf("  %d. %-20s -> %-20s (%.1f KM)%n", 
                i + 1, from, to, dist);
        }
        
        System.out.println("-".repeat(60));
        System.out.printf("Total Distance: %.1f KM%n", totalDistance);
        System.out.printf("Number of Stops: %d%n", stops);
        System.out.printf("Nodes Explored: %d%n", nodesExplored);
        System.out.printf("Execution Time: %.3f ms%n", timeMs);
        System.out.println("=".repeat(80));
    }
    
    // ============================================
    // VIEW METHODS
    // ============================================
    
    private static void showAllRoutes() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("ALL BUS ROUTES");
        System.out.println("=".repeat(80));
        
        System.out.println("\nComplete Bus Network (with distances in KM):");
        System.out.println("-".repeat(60));
        
        for (int i = 0; i < stations.size(); i++) {
            String from = stations.get(i);
            System.out.printf("%-20s -> ", from);
            List<String> connections = new ArrayList<>();
            for (int j = 0; j < stations.size(); j++) {
                if (j > i) {
                    String to = stations.get(j);
                    double dist = dijkstra.getWeight(from, to);
                    if (dist < Double.MAX_VALUE) {
                        connections.add(to + " (" + String.format("%.1f", dist) + " KM)");
                    }
                }
            }
            if (!connections.isEmpty()) {
                System.out.println(String.join(", ", connections));
            } else {
                System.out.println("No outgoing routes");
            }
        }
        System.out.println("=".repeat(80));
    }
    
    private static void showNetworkDetails() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("NETWORK DETAILS");
        System.out.println("=".repeat(80));
        
        System.out.println("\nDegree Analysis:");
        System.out.println("-".repeat(50));
        int maxDegree = 0;
        String mostConnected = "";
        
        for (int i = 0; i < stations.size(); i++) {
            String station = stations.get(i);
            int degree = 0;
            for (int j = 0; j < stations.size(); j++) {
                if (i != j && dijkstra.getWeight(station, stations.get(j)) < Double.MAX_VALUE) {
                    degree++;
                }
            }
            String bar = "=".repeat(Math.min(degree, 10));
            System.out.printf("  %-20s : Degree %2d %s%n", station, degree, bar);
            
            if (degree > maxDegree) {
                maxDegree = degree;
                mostConnected = station;
            }
        }
        
        System.out.println("\nMost Connected: " + mostConnected + " (Degree: " + maxDegree + ")");
        
        System.out.println("\nDistance Matrix (KM):");
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
                    System.out.printf("%-8s", ".");
                } else {
                    double dist = dijkstra.getWeight(row, col);
                    if (dist < Double.MAX_VALUE) {
                        System.out.printf("%-8.1f", dist);
                    } else {
                        System.out.printf("%-8s", "-");
                    }
                }
            }
            System.out.println();
        }
        
        System.out.println("\nAlgorithm Information:");
        System.out.println("-".repeat(50));
        System.out.println("  BFS       : Finds path with minimum number of stops");
        System.out.println("  DFS       : Depth-first search (explores deep first)");
        System.out.println("  Dijkstra  : Finds path with shortest total distance");
        System.out.println("  A*        : Finds shortest path using heuristic (faster)");
        
        System.out.println("\nRecommendation:");
        System.out.println("-".repeat(50));
        System.out.println("  BFS is recommended for bus route finding because:");
        System.out.println("  1. It guarantees the fewest number of bus stops");
        System.out.println("  2. Passengers prefer fewer transfers");
        System.out.println("  3. It is efficient for sparse bus networks");
        System.out.println("  4. It terminates as soon as destination is found");
        
        System.out.println("=".repeat(80));
    }
}