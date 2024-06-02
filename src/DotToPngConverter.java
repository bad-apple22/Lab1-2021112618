
import java.io.*;

public class DotToPngConverter {

    public static void main(String[] args) {
        String dotFilePath = "G:\\homework\\software1\\untitled\\myGraph.dot"; // 输入的.dot文件路径
        String pngFilePath = "G:\\homework\\software1\\untitled\\graph.png"; // 输出的.png文件路径

        convertDotToPng(dotFilePath, pngFilePath);
    }

    public static void convertDotToPng(String dotFilePath, String pngFilePath) {
        try {
            // 执行命令
            String dotCommand = "F:\\Graphviz\\bin\\dot"; // Graphviz的完整路径
            String[] cmd = {dotCommand, "-Tpng", dotFilePath, "-o", pngFilePath};
            Process p = Runtime.getRuntime().exec(cmd);

            // 等待命令执行完成
            p.waitFor();

            // 检查是否有错误输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            // 输出执行结果
            System.out.println("Dot文件转换完成，输出为：" + pngFilePath);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}
