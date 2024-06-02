import java.io.*;
import java.util.*;


public class TextGraph {
    private Map<String, Map<String, Integer>> graph = new HashMap<>();

    public void run() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入文本文件路径：");
        String filePath = scanner.nextLine();

        try {
            readFile(filePath);
            System.out.println("图结构已生成。");
        } catch (IOException e) {
            System.err.println("文件读取失败：" + e.getMessage());
            return;
        }

        while (true) {
            System.out.println("选择功能：\n1. 展示有向图\n2. 查询桥接词\n3. 生成新文本\n4. 计算最短路径\n5. 随机游走\n0. 退出");
            int choice = scanner.nextInt();
            scanner.nextLine();  // Consume newline

            switch (choice) {
                case 1:
                    showDirectedGraph();

                    String dotFilePath = "G:/homework/software1/untitled/myGraph.dot"; // 替换为你想保存DOT文件的实际路径
                    try {
                        writeToDotFile(graph, dotFilePath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    System.out.println("输入两个单词：");
                    String word1 = scanner.next();
                    String word2 = scanner.next();
                    System.out.println(queryBridgeWords(word1, word2));
                    break;
                case 3:
                    System.out.println("输入文本：");
                    String inputText = scanner.nextLine();
                    System.out.println(insertBridgeWords(inputText));
                    break;
                case 4:
                    System.out.println("输入两个单词：");
                    String start = scanner.next();
                    String end = scanner.next();
                    System.out.println(calcShortestPath(start, end));
                    break;
                case 5:
                    RandomWalk randomWalk = new RandomWalk(graph);
                    Thread randomWalkThread = new Thread(() -> {
                        randomWalk.startRandomWalk();
                    });
                    randomWalkThread.start();
                    System.out.println("按Enter键停止遍历...");
                    scanner.nextLine(); // 等待用户输入
                    randomWalk.stopRandomWalk();
                    try {
                        randomWalkThread.join(); // 等待randomWalkThread线程结束
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    System.out.println("遍历已停止");

                    break;
                case 0:
                    return;
                default:
                    System.out.println("无效的选择，请重试。");
            }
        }
    }

    //读文件
    public void readFile(String filePath) throws IOException {
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

    //展示有向图，终端打印
    private void showDirectedGraph() {
        for (Map.Entry<String, Map<String, Integer>> entry : graph.entrySet()) {
            System.out.print(entry.getKey() + " -> ");
            for (Map.Entry<String, Integer> edge : entry.getValue().entrySet()) {
                System.out.print(edge.getKey() + "(" + edge.getValue() + ") ");
            }
            System.out.println();
        }
    }

    //写dot
    public void writeToDotFile(Map<String, Map<String, Integer>> graph, String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("digraph G {");
            writer.newLine();

            for (Map.Entry<String, Map<String, Integer>> entry : graph.entrySet()) {
                String fromNode = entry.getKey();
                Map<String, Integer> edges = entry.getValue();

                for (Map.Entry<String, Integer> edge : edges.entrySet()) {
                    String toNode = edge.getKey();
                    int weight = edge.getValue();
                    writer.write("\"" + fromNode + "\" -> \"" + toNode + "\" [label=\"" + weight + "\"];");
                    writer.newLine();
                }
            }

            writer.write("}");
        }

    }
    //查询桥接词
    private String queryBridgeWords(String word1, String word2) {
        if (!graph.containsKey(word1) || !graph.containsKey(word2)) {
            return "No word1 or word2 in the graph!";
        }

        List<String> bridgeWords = new ArrayList<>();
        Map<String, Integer> edges1 = graph.get(word1);
        for (String intermediate : edges1.keySet()) {
            if (graph.containsKey(intermediate) && graph.get(intermediate).containsKey(word2)) {
                bridgeWords.add(intermediate);
            }
        }

        if (bridgeWords.isEmpty()) {
            return "No bridge words from " + word1 + " to " + word2 + "!";
        } else if (bridgeWords.size() == 1) {
            return "The bridge word from " + word1 + " to " + word2 + " is: " + bridgeWords.get(0) + ".";
        } else {
            StringBuilder result = new StringBuilder("The bridge words from " + word1 + " to " + word2 + " are: ");
            for (int i = 0; i < bridgeWords.size(); i++) {
                result.append(bridgeWords.get(i));
                if (i < bridgeWords.size() - 1) {
                    result.append(", ");
                }
            }
            result.append(".");
            return result.toString();
        }
    }
    //插入桥接词
    public String insertBridgeWords(String text) {
        String[] words = text.split(" ");
        StringBuilder newText = new StringBuilder();

        for (int i = 0; i < words.length - 1; i++) {
            String word1 = words[i];
            String word2 = words[i + 1];

            newText.append(word1).append(" ");
            String bridgeWord = findRandomBridgeWord(word1, word2);
            if (bridgeWord != null) {
                newText.append(bridgeWord).append(" ");
            }
        }
        newText.append(words[words.length - 1]); // 添加最后一个单词

        return newText.toString();
    }
    //随机输出一个桥接词
    private String findRandomBridgeWord(String word1, String word2) {
        List<String> bridgeWords = findBridgeWords(word1, word2);
        if (bridgeWords.isEmpty()) {
            return null;
        } else if (bridgeWords.size() == 1) {
            return bridgeWords.get(0);
        } else {
            return bridgeWords.get(new Random().nextInt(bridgeWords.size()));
        }
    }
    //找到桥接词
    private List<String> findBridgeWords(String word1, String word2) {
        List<String> bridgeWords = new ArrayList<>();
        if (!graph.containsKey(word1) || !graph.containsKey(word2)) {
            return bridgeWords;
        }

        Map<String, Integer> edges1 = graph.get(word1);
        for (String intermediate : edges1.keySet()) {
            if (graph.containsKey(intermediate) && graph.get(intermediate).containsKey(word2)) {
                bridgeWords.add(intermediate);
            }
        }

        return bridgeWords;
    }

    //计算最短路径
    private String calcShortestPath(String word1, String word2) {
        // 检查起始单词和结束单词是否在图中
        if (!graph.containsKey(word1) || !graph.containsKey(word2)) {
            return "起始单词或结束单词不存在于图中。";
        }

        // 初始化距离和前驱节点的映射
        Map<String, Integer> distances = new HashMap<>();
        Map<String, String> predecessors = new HashMap<>();

        // 使用优先队列来管理节点的处理顺序，按距离的升序排列
        PriorityQueue<String> pq = new PriorityQueue<>(Comparator.comparingInt(distances::get));

        // 设置起始节点的距离为0，其他节点的距离默认为无穷大
        distances.put(word1, 0);
        pq.add(word1);

        // 主循环，直到处理完所有节点或找到目标节点
        while (!pq.isEmpty()) {
            // 取出距离最近的节点
            String current = pq.poll();

            // 如果已经到了目标节点，跳出循环
            if (current.equals(word2)) break;

            // 遍历当前节点的所有邻居
            for (Map.Entry<String, Integer> neighbor : graph.get(current).entrySet()) {
                // 计算从当前节点到邻居节点的新距离
                int newDist = distances.get(current) + neighbor.getValue();

                // 如果新距离比已有的距离短，更新距离和前驱节点
                if (newDist < distances.getOrDefault(neighbor.getKey(), Integer.MAX_VALUE)) {
                    distances.put(neighbor.getKey(), newDist);
                    predecessors.put(neighbor.getKey(), current);
                    pq.add(neighbor.getKey());
                }
            }
        }

        // 如果前驱节点中没有目标节点，说明没有路径
        if (!predecessors.containsKey(word2)) {
            return "没有路径从 " + word1 + " 到 " + word2;
        }

        // 反向构建从终点到起点的路径
        List<String> path = new LinkedList<>();
        for (String at = word2; at != null; at = predecessors.get(at)) {
            path.add(at);
        }

        // 反转路径，使其从起点到终点
        Collections.reverse(path);
        String dotFilePath = "G:/homework/software1/untitled/myGraph1.dot";
        try {
            writeToDotFile1(graph, dotFilePath, path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // 返回路径的字符串表示形式
        return String.join(" -> ", path);
    }

    public void writeToDotFile1(Map<String, Map<String, Integer>> graph, String filePath, List<String> path) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("digraph G {");
            writer.newLine();
            int length = 0;

            for (Map.Entry<String, Map<String, Integer>> entry : graph.entrySet()) {
                String fromNode = entry.getKey();
                Map<String, Integer> edges = entry.getValue();

                for (Map.Entry<String, Integer> edge : edges.entrySet()) {
                    String toNode = edge.getKey();
                    int weight = edge.getValue();

                    boolean isShortestEdge = path.contains(fromNode) && path.contains(toNode)
                            && path.indexOf(toNode) == path.indexOf(fromNode) + 1;
                    if (isShortestEdge) {
                        writer.write("\"" + fromNode + "\" -> \"" + toNode + "\" [label=\"" + weight + "\", color=\"red\"];");
                        length = length + weight;
                    } else {
                        writer.write("\"" + fromNode + "\" -> \"" + toNode + "\" [label=\"" + weight + "\"];");
                    }

                    writer.newLine();
                }
            }
            System.out.println("路径长度：" + length);
            writer.write("}");
        }
    }


}


