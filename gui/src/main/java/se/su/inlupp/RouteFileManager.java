package se.su.inlupp;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;

public class RouteFileManager {
    private RouteFileManager() {}

    public static void saveTXT(Graph<String> graph, Map<String, GuiNode> nodeMap, String imagePath) throws IOException {
        PrintWriter writer = new PrintWriter("route.txt");

        writer.println("IMAGE:" + imagePath);

        for (String node : graph.getNodes()) {
            GuiNode visualNode = nodeMap.get(node);
            writer.println("NODE:" + node + ":" + visualNode.getLayoutX() + ":" + visualNode.getLayoutY());
        }
        for (String node : graph.getNodes()) {
            for (Edge<String> edge : graph.getEdgesFrom(node)) {
                writer.println(
                        "EDGE:" + node + ":" + edge.getDestination() + ":" + edge.getName() + ":" + edge.getWeight());
            }
        }
        writer.close();
    }

    public static void loadTXT(Graph<String> graph, Pane nodeArea, Map<String, GuiNode> nodeMap, FlowPane nodeControls, String[] imagePathHolder)
            throws IOException {
        nodeArea.getChildren().clear();
        nodeArea.getChildren().add(nodeControls);
        FileReader fileReader = new FileReader("route.txt");
        BufferedReader reader = new BufferedReader(fileReader);
        List<String[]> edgeList = new ArrayList<>();

        String line;

        while ((line = reader.readLine()) != null) {
            if (line.startsWith("IMAGE:")) {
                imagePathHolder[0] = line.substring(6);
            } else if (line.startsWith("NODE:")) {
                String[] parts = line.split(":");
                String nodeName = parts[1];
                double x = Double.parseDouble(parts[2]);
                double y = Double.parseDouble(parts[3]);
                graph.add(nodeName);
                GuiNode visualNode = new GuiNode(x, y, nodeName);
                nodeArea.getChildren().add(visualNode);
            } else if (line.startsWith("EDGE:")) {
                String[] parts = line.split(":");
                edgeList.add(parts);
            }
        }
        reader.close();
        fileReader.close();
        for (String[] edge : edgeList) {
            String from = edge[1];
            String to = edge[2];
            String name = edge[3];
            int weight = Integer.parseInt(edge[4]);

            graph.connect(from, to, name, weight);

            GuiNode startNode = nodeMap.get(from);
            GuiNode endNode = nodeMap.get(to);

            Edge<String> guiEdge = graph.getEdgeBetween(from, to);
            GuiLine newLine = new GuiLine(startNode, endNode, guiEdge);
            nodeArea.getChildren().add(newLine);
        }
    }

    public static void savePNG(Pane nodeArea) throws IOException {
        File file = new File("route.png");
        WritableImage image = nodeArea.snapshot(null, null);
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        ImageIO.write(bufferedImage, "png", file);
    }

    public static ImageView loadPNG(String imagePath) throws IOException {
        File file = new File(imagePath);

        if (!file.exists()) {
            throw new IOException("Image file not found.");
        }
        Image image = new Image(file.toURI().toString());
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        return imageView;

    }
}
