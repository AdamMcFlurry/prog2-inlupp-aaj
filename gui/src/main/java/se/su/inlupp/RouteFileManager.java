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
import java.util.Set;
import java.util.HashSet;

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
        Set<String> savedEdges = new HashSet<>();
        writer.println("IMAGE:" + imagePath);

        for (String node : graph.getNodes()) {
            GuiNode visualNode = nodeMap.get(node);
            writer.println("NODE:" + node + ":" + visualNode.getLayoutX() + ":" + visualNode.getLayoutY());
        }
        for (String node : graph.getNodes()) {
            for (Edge<String> edge : graph.getEdgesFrom(node)) {

                String destination = edge.getDestination();
                String edgeKey;

                if (node.compareTo(destination) < 0) {
                    edgeKey = node + ":" + destination;
                } else {
                    edgeKey = destination + ":" + node;
                }
                if (!savedEdges.contains(edgeKey)) {
                    savedEdges.add(edgeKey);
                
                writer.println(
                        "EDGE:" + node + ":" + edge.getDestination() + ":" + edge.getName() + ":" + edge.getWeight());
                }
            }
        }
        writer.close();
    }

    public static RouteData loadTXT(Graph<String> graph)throws IOException {

        RouteData routeData = new RouteData();
        FileReader fileReader = new FileReader("route.txt");
        BufferedReader reader = new BufferedReader(fileReader);

        String line;

        while ((line = reader.readLine()) != null) {
            if (line.startsWith("IMAGE:")) {
                routeData.setImagePath(line.substring(6));
            } else if (line.startsWith("NODE:")) {
                String[] parts = line.split(":");
                String nodeName = parts[1];
                double x = Double.parseDouble(parts[2]);
                double y = Double.parseDouble(parts[3]);
                graph.add(nodeName);
                routeData.getNodes().put(nodeName, new double[] {x, y});
            } else if (line.startsWith("EDGE:")) {
                String[] parts = line.split(":");
                String from = parts[1];
                String to = parts[2];
                String name = parts[3];
                int weight = Integer.parseInt(parts[4]);

                graph.connect(from, to, name, weight);
                
                routeData.getEdges().add(parts);
            }
        }
        reader.close();
        fileReader.close();

        return routeData;
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
