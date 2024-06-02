import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;

public class TextGraphGUI extends JFrame {
    private Map<String, Map<String, Integer>> graph = new HashMap<>();
    private JTextArea outputArea;

    public TextGraphGUI() {
        setTitle("Text Graph GUI");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem loadItem = new JMenuItem("Load Text File");

        loadItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));
                int result = fileChooser.showOpenDialog(TextGraphGUI.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    try {
                        readFile(selectedFile.getAbsolutePath());
                        outputArea.setText("File loaded and graph generated successfully.");
                    } catch (IOException ex) {
                        outputArea.setText("Failed to read file: " + ex.getMessage());
                    }
                }
            }
        });

        fileMenu.add(loadItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(5, 2));

        JButton showGraphButton = new JButton("Show Directed Graph");
        showGraphButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showDirectedGraph();
            }
        });
        controlPanel.add(showGraphButton);

        JTextField bridgeWordField1 = new JTextField();
        JTextField bridgeWordField2 = new JTextField();
        JButton bridgeWordButton = new JButton("Query Bridge Words");
        bridgeWordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String word1 = bridgeWordField1.getText().trim();
                String word2 = bridgeWordField2.getText().trim();
                outputArea.setText(queryBridgeWords(word1, word2));
            }
        });
        controlPanel.add(new JLabel("Word 1:"));
        controlPanel.add(bridgeWordField1);
        controlPanel.add(new JLabel("Word 2:"));
        controlPanel.add(bridgeWordField2);
        controlPanel.add(bridgeWordButton);

        JTextField newTextInput = new JTextField();
        JButton generateTextButton = new JButton("Generate New Text");
        generateTextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String inputText = newTextInput.getText().trim();
                outputArea.setText(generateNewText(inputText));
            }
        });
        controlPanel.add(new JLabel("Input Text:"));
        controlPanel.add(newTextInput);
        controlPanel.add(generateTextButton);

        JTextField shortestPathField1 = new JTextField();
        JTextField shortestPathField2 = new JTextField();
        JButton shortestPathButton = new JButton("Calculate Shortest Path");
        shortestPathButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String word1 = shortestPathField1.getText().trim();
                String word2 = shortestPathField2.getText().trim();
                outputArea.setText(calcShortestPath(word1, word2));
            }
        });
        controlPanel.add(new JLabel("Start Word:"));
        controlPanel.add(shortestPathField1);
        controlPanel.add(new JLabel("End Word:"));
        controlPanel.add(shortestPathField2);
        controlPanel.add(shortestPathButton);

        JButton randomWalkButton = new JButton("Random Walk");
        randomWalkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                outputArea.setText(randomWalk());
            }
        });
        controlPanel.add(randomWalkButton);

        panel.add(controlPanel, BorderLayout.SOUTH);
        add(panel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new TextGraphGUI().setVisible(true);
            }
        });
    }

    private void readFile(String filePath) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        String line;
        StringBuilder content = new StringBuilder();

        while ((line = br.readLine()) != null) {
            content.append(line.replaceAll("[^A-Za-z]", " ").toLowerCase()).append(" ");
        }
        br.close();

        String[] words = content.toString().split("\\s+");
        for (int i = 0; i < words.length - 1; i++) {
            String word1 = words[i];
            String word2 = words[i + 1];
            if (!word1.isEmpty() && !word2.isEmpty()) {
                graph.putIfAbsent(word1, new HashMap<>());
                Map<String, Integer> edges = graph.get(word1);
                edges.put(word2, edges.getOrDefault(word2, 0) + 1);
            }
        }
    }

    private void showDirectedGraph() {
        StringBuilder graphStr = new StringBuilder();
        for (Map.Entry<String, Map<String, Integer>> entry : graph.entrySet()) {
            graphStr.append(entry.getKey()).append(" -> ");
            for (Map.Entry<String, Integer> edge : entry.getValue().entrySet()) {
                graphStr.append(edge.getKey()).append("(").append(edge.getValue()).append(") ");
            }
            graphStr.append("\n");
        }
        outputArea.setText(graphStr.toString());
    }

    private String queryBridgeWords(String word1, String word2) {
        if (!graph.containsKey(word1)) {
            return "单词 " + word1 + " 不存在于图中。";
        }
        Map<String, Integer> edges = graph.get(word1);
        for (String intermediate : edges.keySet()) {
            if (graph.containsKey(intermediate) && graph.get(intermediate).containsKey(word2)) {
                return "桥接词是：" + intermediate;
            }
        }
        return "没有桥接词。";
    }

    private String generateNewText(String inputText) {
        String[] words = inputText.toLowerCase().split("\\s+");
        StringBuilder newText = new StringBuilder();
        for (int i = 0; i < words.length - 1; i++) {
            newText.append(words[i]).append(" ");
            String bridgeWord = queryBridgeWords(words[i], words[i + 1]);
            if (bridgeWord.startsWith("桥接词是：")) {
                newText.append(bridgeWord.substring(5)).append(" ");
            }
        }
        newText.append(words[words.length - 1]);
        return newText.toString();
    }

    private String calcShortestPath(String word1, String word2) {
        if (!graph.containsKey(word1) || !graph.containsKey(word2)) {
            return "起始单词或结束单词不存在于图中。";
        }
        Map<String, Integer> distances = new HashMap<>();
        Map<String, String> predecessors = new HashMap<>();
        PriorityQueue<String> pq = new PriorityQueue<>(Comparator.comparingInt(distances::get));
        distances.put(word1, 0);
        pq.add(word1);

        while (!pq.isEmpty()) {
            String current = pq.poll();
            if (current.equals(word2)) break;
            for (Map.Entry<String, Integer> neighbor : graph.get(current).entrySet()) {
                int newDist = distances.get(current) + neighbor.getValue();
                if (newDist < distances.getOrDefault(neighbor.getKey(), Integer.MAX_VALUE)) {
                    distances.put(neighbor.getKey(), newDist);
                    predecessors.put(neighbor.getKey(), current);
                    pq.add(neighbor.getKey());
                }
            }
        }

        if (!predecessors.containsKey(word2)) {
            return "没有路径从 " + word1 + " 到 " + word2;
        }

        java.util.List<String> path = new LinkedList<>();
        for (String at = word2; at != null; at = predecessors.get(at)) {
            path.add(at);
        }
        Collections.reverse(path);
        return String.join(" -> ", path);
    }

    private String randomWalk() {
        if (graph.isEmpty()) {
            return "图为空。";
        }

        java.util.List<String> nodes = new ArrayList<>(graph.keySet());
        Random random = new Random();
        String current = nodes.get(random.nextInt(nodes.size()));
        StringBuilder walk = new StringBuilder(current);

        for (int i = 0; i < 10; i++) {
            Map<String, Integer> edges = graph.get(current);
            if (edges == null || edges.isEmpty()) break;
            java.util.List<String> neighbors = new ArrayList<>(edges.keySet());
            current = neighbors.get(random.nextInt(neighbors.size()));
            walk.append(" -> ").append(current);
        }

        return walk.toString();
    }
}
