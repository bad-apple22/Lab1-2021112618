import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class random {
    private static volatile boolean stopRequested = false;

    public static void main(String[] args) {

        Thread walkerThread = new Thread(new RandomWalker());
        walkerThread.start();

        Scanner scanner = new Scanner(System.in);
        System.out.println("Press Enter to stop the random walk.");
        scanner.nextLine();

        stopRequested = true;
        walkerThread.interrupt();
    }

    static class RandomWalker implements Runnable {

        private Map<String, Map<String, Integer>> graph;
    private Set<String> visitedNodes;
    private Set<String> visitedEdges;
    private boolean shouldStop;
        private final Random random = new Random();

        @Override
        public void run() {
String currentNode = getRandomNode();
    List<String> path = new ArrayList<>();
    path.add(currentNode);
    visitedNodes.add(currentNode);
            while (currentNode != null && !shouldStop) {
             Map<String, Integer> neighbors = graph.get(currentNode);
    if (neighbors == null || neighbors.isEmpty()) {
        break; // No outgoing edges
    }


            String nextNode = getRandomNeighbor(currentNode, neighbors);
            String edge = currentNode + "->" + nextNode;

            if (visitedEdges.contains(edge)) {
                path.add(nextNode);
                break; // Repeated edge
            }

            visitedEdges.add(edge);
            path.add(nextNode);
            visitedNodes.add(nextNode);
            currentNode = nextNode;
        }
                try {
                    Thread.sleep(1000); // Sleep for 1 second
                } catch (InterruptedException e) {
                    // Thread interrupted, check if stop is requested
                    if (stopRequested) {
                        System.out.println("Random walk stopped.");
                        return;
                    }
                }
            }


private String getRandomNode() {
        List<String> nodes = new ArrayList<>(graph.keySet());
        return nodes.get(new Random().nextInt(nodes.size()));
    }
    private String getRandomNeighbor(String node, Map<String, Integer> neighbors) {
        List<String> neighborList = new ArrayList<>(neighbors.keySet());
        return neighborList.get(new Random().nextInt(neighborList.size()));
    }

    private void writePathToFile(List<String> path) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("random_walk_path.txt"))) {
            for (String node : path) {
                writer.write(node);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopRandomWalk() {
        this.shouldStop = true;
    }
        }
    }
