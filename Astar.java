import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

class Astar {
   // Graph representation: Adjacency List
    private Map<String, List<Route>> graph;
    private String startStation;
    private String goalStation;
    
    // Route class representing a connection between stations
    public static class Route {
        String destination;
        double distance;  // Distance in KM
        
        Route(String destination, double distance) {
            this.destination = destination;
            this.distance = distance;
        }
    }
    
    public Astar(Map<String, List<Route>> graph, String startStation, String goalStation) {
        this.graph = graph;
        this.startStation = startStation;
        this.goalStation = goalStation;
    }
    
    // Heuristic: Straight-line distance estimation
    private double heuristic(String current, String goal) {
        Map<String, Map<String, Double>> heuristicMap = getHeuristicMap();
        
        if (heuristicMap.containsKey(current) && heuristicMap.get(current).containsKey(goal)) {
            return heuristicMap.get(current).get(goal);
        }
        
        // If no heuristic available, return 0 (turns A* into Dijkstra)
        return 0;
    }
    
    // Pre-defined heuristic values (in KM)
    private Map<String, Map<String, Double>> getHeuristicMap() {
        Map<String, Map<String, Double>> h = new HashMap<>();
        
        // Heuristic values for each station
        // These are estimated straight-line distances to each destination
        
        // Batu Ferringhi
        Map<String, Double> bf_h = new HashMap<>();
        bf_h.put("Tanjung Bungah", 5.7);
        bf_h.put("Pulau Tikus", 13.0);
        h.put("Batu Ferringhi", bf_h);
        
        // Tanjung Bungah
        Map<String, Double> tb_h = new HashMap<>();
        tb_h.put("Batu Ferringhi", 5.7);
        tb_h.put("Straits Quay", 4.3);
        tb_h.put("Pulau Tikus", 7.5);
        h.put("Tanjung Bungah", tb_h);
        
        // Straits Quay
        Map<String, Double> sq_h = new HashMap<>();
        sq_h.put("Tanjung Bungah", 4.3);
        sq_h.put("Pulau Tikus", 5.9);
        sq_h.put("Gurney Drive", 4.4);
        sq_h.put("Komtar", 7.9);
        h.put("Straits Quay", sq_h);
        
        // Pulau Tikus
        Map<String, Double> pt_h = new HashMap<>();
        pt_h.put("Batu Ferringhi", 13.0);
        pt_h.put("Tanjung Bungah", 7.5);
        pt_h.put("Straits Quay", 5.9);
        pt_h.put("Gurney Drive", 1.5);
        pt_h.put("Komtar", 4.4);
        h.put("Pulau Tikus", pt_h);
        
        // Gurney Drive
        Map<String, Double> gd_h = new HashMap<>();
        gd_h.put("Straits Quay", 4.4);
        gd_h.put("Komtar", 3.5);
        gd_h.put("Weld Quay", 4.9);
        h.put("Gurney Drive", gd_h);
        
        // Komtar
        Map<String, Double> komtar_h = new HashMap<>();
        komtar_h.put("Straits Quay", 7.9);
        komtar_h.put("Pulau Tikus", 4.4);
        komtar_h.put("Gurney Drive", 3.5);
        komtar_h.put("Weld Quay", 1.5);
        komtar_h.put("Sungai Pinang", 2.4);
        komtar_h.put("USM Gelugor", 9.0);
        h.put("Komtar", komtar_h);
        
        // Weld Quay
        Map<String, Double> wq_h = new HashMap<>();
        wq_h.put("Gurney Drive", 4.9);
        wq_h.put("Komtar", 1.5);
        h.put("Weld Quay", wq_h);
        
        // Sungai Pinang
        Map<String, Double> sp_h = new HashMap<>();
        sp_h.put("Komtar", 2.4);
        sp_h.put("Jelutong", 2.8);
        h.put("Sungai Pinang", sp_h);
        
        // Jelutong
        Map<String, Double> jelutong_h = new HashMap<>();
        jelutong_h.put("Sungai Pinang", 2.8);
        jelutong_h.put("USM Gelugor", 5.8);
        h.put("Jelutong", jelutong_h);
        
        // USM Gelugor
        Map<String, Double> usm_h = new HashMap<>();
        usm_h.put("Komtar", 9.0);
        usm_h.put("Jelutong", 5.8);
        h.put("USM Gelugor", usm_h);
        
        return h;
    }
    
    // Get neighbors (connected stations) from the graph
    private List<Route> getNeighbors(String station) {
        if (graph.containsKey(station)) {
            return graph.get(station);
        }
        return new ArrayList<>();
    }
    
    // Convert route to key for HashMap
    private String stationKey(String station) {
        return station;
    }
    
    // Reconstruct the path from cameFrom map
    private List<String> reconstructPath(Map<String, String> cameFrom, String current) {
        List<String> path = new ArrayList<>();
        
        while (cameFrom.containsKey(current)) {
            path.add(current);
            current = cameFrom.get(current);
        }
        path.add(startStation);
        Collections.reverse(path);
        return path;
    }

    // Solve the bus route using A* algorithm
    public Map<String, Object> solve() {
        // Open list as ArrayList
        List<Object[]> openList = new ArrayList<>();
        
        // Maps to store g(n) values and parent relationships
        Map<String, Double> gn = new HashMap<>();
        Map<String, String> cameFrom = new HashMap<>();
        Set<String> closedSet = new HashSet<>();

        // Initialize with start node
        double startH = heuristic(startStation, goalStation);
        openList.add(new Object[]{startH, 0.0, startStation});
        gn.put(stationKey(startStation), 0.0);

        while (!openList.isEmpty()) {
            // Sort by f(n) = g(n) + h(n)
            openList.sort((a, b) -> {
                double fA = (double)a[1] + (double)a[0];
                double fB = (double)b[1] + (double)b[0];
                return Double.compare(fA, fB);
            });

            // Get the best node
            Object[] currentObj = openList.remove(0);
            double currentH = (double)currentObj[0];
            double currentG = (double)currentObj[1];
            String currentStation = (String)currentObj[2];
            String currentKey = stationKey(currentStation);

            // Skip if already explored
            if (closedSet.contains(currentKey)) {
                continue;
            }

            closedSet.add(currentKey);

            // Check if goal reached
            if (currentStation.equals(goalStation)) {
                List<String> path = reconstructPath(cameFrom, currentStation);
                
                // Calculate total distance
                double totalDistance = calculateTotalDistance(path);
                
                Map<String, Object> result = new HashMap<>();
                result.put("found", true);
                result.put("path", path);
                result.put("totalDistance", totalDistance);
                result.put("stops", path.size() - 1);
                return result;
            }

            // Explore neighbors (connected stations)
            for (Route route : getNeighbors(currentStation)) {
                String neighbor = route.destination;
                double distance = route.distance;
                String neighborKey = stationKey(neighbor);

                if (closedSet.contains(neighborKey)) {
                    continue;
                }

                double newG = currentG + distance;

                if (!gn.containsKey(neighborKey) || newG < gn.get(neighborKey)) {
                    cameFrom.put(neighborKey, currentStation);
                    gn.put(neighborKey, newG);
                    double hn = heuristic(neighbor, goalStation);
                    openList.add(new Object[]{hn, newG, neighbor});
                }
            }
        }

        // Path not found
        Map<String, Object> result = new HashMap<>();
        result.put("found", false);
        return result;
    }
    
    // Calculate total distance for the path
    private double calculateTotalDistance(List<String> path) {
        double totalDistance = 0.0;
        for (int i = 0; i < path.size() - 1; i++) {
            String from = path.get(i);
            String to = path.get(i + 1);
            
            // Find the distance between these two stations
            List<Route> routes = graph.get(from);
            for (Route route : routes) {
                if (route.destination.equals(to)) {
                    totalDistance += route.distance;
                    break;
                }
            }
        }
        return totalDistance;
    }
    
    // Print the bus route
    public void printRoute(Map<String, Object> result) {
        if (!(boolean) result.get("found")) {
            System.out.println("No bus route found between " + startStation + " and " + goalStation);
            return;
        }

        @SuppressWarnings("unchecked")
        List<String> path = (List<String>) result.get("path");
        double totalDistance = (double) result.get("totalDistance");
        int stops = (int) result.get("stops");

        System.out.println("=".repeat(80));
        System.out.println("PENANG BUS ROUTE FOUND!");
        System.out.println("=".repeat(80));
        
        System.out.println("\nRoute from " + startStation + " to " + goalStation + ":");
        System.out.println("-".repeat(60));
        
        // Print the route with arrows
        StringBuilder routeStr = new StringBuilder();
        for (int i = 0; i < path.size(); i++) {
            routeStr.append(path.get(i));
            if (i < path.size() - 1) {
                routeStr.append(" --> ");
            }
        }
        System.out.println(routeStr.toString());
        
        System.out.println("\nRoute Details:");
        System.out.println("-".repeat(60));
        
        // Print each leg of the journey
        for (int i = 0; i < path.size() - 1; i++) {
            String from = path.get(i);
            String to = path.get(i + 1);
            
            // Find distance
            double legDistance = 0.0;
            List<Route> routes = graph.get(from);
            for (Route route : routes) {
                if (route.destination.equals(to)) {
                    legDistance = route.distance;
                    break;
                }
            }
            System.out.printf("%d. %-20s --> %-20s (%.1f KM)%n", 
                            (i + 1), from, to, legDistance);
        }
        
        System.out.println("-".repeat(60));
        System.out.printf("Total Stops: %d%n", stops);
        System.out.printf("Total Distance: %.1f KM%n", totalDistance);
    }    
}
