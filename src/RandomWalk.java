
import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.concurrent.atomic.AtomicBoolean;
import java.lang.InterruptedException;


public class RandomWalk {

    private Map<String, Map<String, Integer>> graph;
    private Set<String> visitedNodes;
    private Set<String> visitedEdges;
    private boolean shouldStop;

    public RandomWalk(Map<String, Map<String, Integer>> graph) {
        this.graph = graph;
        this.visitedNodes = new HashSet<>();
        this.visitedEdges = new HashSet<>();
        this.shouldStop = false;
    }


public void startRandomWalk() {
    String currentNode = getRandomNode();
    List<String> path = new ArrayList<>();
    path.add(currentNode);
    visitedNodes.add(currentNode);
    while (currentNode != null &&!shouldStop) {
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
    try {
                // 暂停3秒（3000毫秒）为了演示暂停
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                // 捕获InterruptedException异常
                break;
            }


    }
    writePathToFile(path);

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
