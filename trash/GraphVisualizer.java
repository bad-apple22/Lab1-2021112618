import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.engine.GraphvizEngine;
import guru.nidi.graphviz.engine.GraphvizJdkEngine;
import guru.nidi.graphviz.engine.GraphvizV8Engine;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.model.MutableNode;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

import static guru.nidi.graphviz.model.Factory.*;

public class GraphVisualizer {
    static {
        // 设置使用Batik引擎
        Graphviz.useEngine(new GraphvizV8Engine(), new GraphvizJdkEngine());
         Graphviz.useEngine(new GraphvizJdkEngine());
    }

    private Map<String, Map<String, Integer>> graph;

    public GraphVisualizer(Map<String, Map<String, Integer>> graph) {
        this.graph = graph;
    }

    public void showDirectedGraph() {
        MutableGraph g = mutGraph("example").setDirected(true);
        Map<String, MutableNode> nodes = new HashMap<>();

        for (String key : graph.keySet()) {
            nodes.put(key, mutNode(key));
        }

        for (Map.Entry<String, Map<String, Integer>> entry : graph.entrySet()) {
            for (Map.Entry<String, Integer> edge : entry.getValue().entrySet()) {
                nodes.get(entry.getKey()).addLink(to(nodes.get(edge.getKey())).with(Label.of(edge.getValue().toString())));
            }
        }

        for (MutableNode node : nodes.values()) {
            g.add(node);
        }

        try {
            BufferedImage image = Graphviz.fromGraph(g).render(Format.PNG).toImage();
            displayImage(image);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String calcShortestPath(String word1, String word2) {
        if (!graph.containsKey(word1) || !graph.containsKey(word2)) {
            return "No path exists between " + word1 + " and " + word2 + ".";
        }

        // Dijkstra算法
        Map<String, Integer> distance = new HashMap<>();
        Map<String, List<String>> prev = new HashMap<>();
        PriorityQueue<String> pq = new PriorityQueue<>(Comparator.comparingInt(distance::get));

        for (String vertex : graph.keySet()) {
            distance.put(vertex, Integer.MAX_VALUE);
            prev.put(vertex, new ArrayList<>());
        }
        distance.put(word1, 0);
        pq.add(word1);

        while (!pq.isEmpty()) {
            String current = pq.poll();
            if (current.equals(word2)) {
                break;
            }
            if (!graph.containsKey(current)) {
                continue;
            }
            for (String neighbor : graph.get(current).keySet()) {
                int newDistance = distance.get(current) + graph.get(current).get(neighbor);
                if (newDistance < distance.get(neighbor)) {
                    distance.put(neighbor, newDistance);
                    prev.get(neighbor).clear();
                    prev.get(neighbor).add(current);
                    pq.add(neighbor);
                } else if (newDistance == distance.get(neighbor)) {
                    prev.get(neighbor).add(current);
                }
            }
        }

        List<List<String>> allPaths = new ArrayList<>();
        findAllPaths(word2, word1, prev, new ArrayList<>(), allPaths);

        StringBuilder result = new StringBuilder();
        if (allPaths.isEmpty()) {
            result.append("No path exists between ").append(word1).append(" and ").append(word2).append(".");
        } else {
            result.append("Shortest paths from ").append(word1).append(" to ").append(word2).append(": \n");
            for (List<String> path : allPaths) {
                Collections.reverse(path);
                for (int i = 0; i < path.size(); i++) {
                    result.append(path.get(i));
                    if (i < path.size() - 1) {
                        result.append(" -> ");
                    }
                }
                result.append(". Path length: ").append(distance.get(word2)).append("\n");
            }
            highlightPathsInGraph(allPaths);
        }
        return result.toString();
    }

    private void findAllPaths(String current, String start, Map<String, List<String>> prev, List<String> path, List<List<String>> allPaths) {
        path.add(current);
        if (current.equals(start)) {
            allPaths.add(new ArrayList<>(path));
        } else {
            for (String p : prev.get(current)) {
                findAllPaths(p, start, prev, path, allPaths);
            }
        }
        path.remove(path.size() - 1);
    }

    private void highlightPathsInGraph(List<List<String>> paths) {
        MutableGraph g = mutGraph("example").setDirected(true);
        Map<String, MutableNode> nodes = new HashMap<>();

        for (String key : graph.keySet()) {
            nodes.put(key, mutNode(key));
        }

        for (Map.Entry<String, Map<String, Integer>> entry : graph.entrySet()) {
            for (Map.Entry<String, Integer> edge : entry.getValue().entrySet()) {
                nodes.get(entry.getKey()).addLink(to(nodes.get(edge.getKey())).with(Label.of(edge.getValue().toString())));
            }
        }

        for (MutableNode node : nodes.values()) {
            g.add(node);
        }

        for (List<String> path : paths) {
            for (int i = 0; i < path.size() - 1; i++) {
                nodes.get(path.get(i)).linkTo(nodes.get(path.get(i + 1))).with(Color.RED, Color.BLACK.font());
            }
        }

        try {
            BufferedImage image = Graphviz.fromGraph(g).render(Format.PNG).toImage();
            displayImage(image);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displayImage(BufferedImage image) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        JLabel label = new JLabel(new ImageIcon(image));
        frame.getContentPane().add(label, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    public static void main(String[] args) {
        Map<String, Map<String, Integer>> graph = new HashMap<>();
        graph.put("A", Map.of("B", 1, "C", 4));
        graph.put("B", Map.of("C", 2, "D", 5));
        graph.put("C", Map.of("D", 1));
        graph.put("D", Map.of());

        GraphVisualizer gv = new GraphVisualizer(graph);
        gv.showDirectedGraph();
        String shortestPathResult = gv.calcShortestPath("A", "D");
        System.out.println(shortestPathResult);
    }
}
