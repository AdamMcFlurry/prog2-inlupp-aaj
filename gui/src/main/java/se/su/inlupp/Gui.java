package se.su.inlupp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import javafx.application.Platform;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

public class Gui extends Application {

    private TextField searchField;
    private Graph<String> graph = new ListGraph<String>();
    ListView<String> listView;
    private BorderPane root;

public void start(Stage stage) {
    graph.add("Stockholm");
    graph.add("Oslo");
    graph.add("Köpenhamn");
    graph.add("Kiruna");

    root = new BorderPane();

    MenuBar menuBar = new MenuBar();
    Menu fileMenu = new Menu("Route");
    menuBar.getMenus().add(fileMenu);

    MenuItem newItem = new MenuItem("New Route");
    fileMenu.getItems().add(newItem);
    newItem.setOnAction(new NewHandler());

    MenuItem saveItem = new MenuItem("Save Route");
    fileMenu.getItems().add(saveItem);
    saveItem.setOnAction(new SaveHandler());

    MenuItem loadItem = new MenuItem("Load Route");
    fileMenu.getItems().add(loadItem);
    loadItem.setOnAction(new LoadHandler());

    MenuItem exitItem = new MenuItem("Exit");
    fileMenu.getItems().add(exitItem);
    exitItem.setOnAction(new ExitHandler());

    root.setTop(menuBar);

    
    listView = new ListView<>();
    listView.setPrefWidth(150);
    ObservableList<String> nodeList = FXCollections.observableArrayList(graph.getNodes());
    FXCollections.sort(nodeList);
    listView.setItems(nodeList);
    
    FlowPane nodeControls = new FlowPane();
    nodeControls.setAlignment(Pos.CENTER);
    nodeControls.setPadding(new Insets(5));
    nodeControls.setHgap(5);

    searchField = new TextField();
    Button searchButton = new Button("Search");
    Button addButton = new Button("Add Node");
    addButton.setOnAction(new AddHandler());
    Button deleteButton = new Button("Delete Node");
    nodeControls.getChildren().addAll(searchField, searchButton, addButton, deleteButton);

    root.setLeft(listView);
    root.setCenter(nodeControls);
    Scene scene = new Scene(root, 640, 480);
    stage.setScene(scene);
    stage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }

  class AddHandler implements EventHandler<ActionEvent> {
        public void handle(ActionEvent event) {
            String word = searchField.getText();
            graph.add(word);

            int index = Collections.binarySearch((listView.getItems()), word);
            if (index < 0) {
                listView.getItems().add(-index - 1, word);
            }
        }
    }
  
  class NewHandler implements EventHandler<ActionEvent>{
    @Override
    public void handle(ActionEvent event) {
      graph = new ListGraph<>();
      AddDefaultStations();
      // Är du säker -sak (F7 - Varning vid osparade ändringar)
      
    }
  }
  class SaveHandler implements EventHandler<ActionEvent>{
    @Override
    public void handle(ActionEvent event) {
      // F6, F9
      // Sparas både som textfil (kan laddas up och manipuleras) och som .png (kan visas men inte manipuleras) Hittade i F14
      //Hur ska vi göra med att välja antingen PNG eller TXT?
      savePNG();
      saveTXT();
    }
  }
  class LoadHandler implements EventHandler<ActionEvent>{
    @Override
    public void handle(ActionEvent event) {
      // F6, F8, F9
      // Ladda upp. Både som textfil (manipuleras) och som .png (inte manipuleras)
      //Hur ska vi göra med att välja antingen PNG eller TXT?
      loadTXT();
      loadPNG();
    }
  }
  class ExitHandler implements EventHandler<ActionEvent>{
    public void handle(ActionEvent event) {
      Platform.exit();
    }
  }
  private void AddDefaultStations() {
    graph.add("T-Centralen");
    graph.add("Hötorget");
    graph.add("Farsta");
    graph.add("Skarpnäck");
    graph.add("Gullmarsplan");
  }

  private void saveTXT() {
    try {
      PrintWriter writer = new PrintWriter("route.txt");

      for (String node : graph.getNodes()){
        writer.println("NODE:" + node);
      }
      for (String node : graph.getNodes()){
        for (Edge<String> edge : graph.getEdgesFrom(node)) {
          writer.println("EDGE;" + node + ";" + edge.getDestination() + ";" + edge.getName() + ";" + edge.getWeight());
        }
      }
      writer.close();

    } catch (Exception e) {
      Alert alert = new Alert(Alert.AlertType.ERROR, "Could not save route.");
      alert.showAndWait();
    }
  }
  private void savePNG() {
    try {
        WritableImage image = root.snapshot(null,null);
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        ImageIO.write(bufferedImage, "png", new File("route.png"));
      } catch (IOException e) {
        Alert alert = new Alert(Alert.AlertType.ERROR,"Could not save PNG");
        alert.showAndWait();
      }
  }
  private void loadTXT() {
    try {
      graph = new ListGraph<>();
      Scanner scanner = new Scanner(new File("route.txt"));
      List<String[]> edgeList = new ArrayList<>();

      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();
        String[] parts = line.split(";");
        if (line.startsWith("NODE:")) {
          String node = line.substring(5);
          graph.add(node);
        }
        else if (parts[0].equals("EDGE:")) {
          edgeList.add(parts);
        }
      }
      scanner.close();
      for (String[] edge : edgeList) {
        graph.connect(edge[1], edge[2], edge[3], Integer.parseInt(edge[4]));
        ObservableList<String> updatedList = FXCollections.observableArrayList(graph.getNodes());
        FXCollections.sort(updatedList);
        listView.setItems(updatedList);
      }
    } catch (Exception e) {
      Alert alert = new Alert(Alert.AlertType.ERROR, "Could not load PNG.");
      alert.showAndWait();
    }
  }
  private void loadPNG() {
    try {
      Image image = new Image(new File("route.png").toURI().toString());
      ImageView imageView = new ImageView(image);
      root.setCenter(imageView);
    } catch (Exception e) {
      Alert alert = new Alert(Alert.AlertType.ERROR, "Could not load PNG.");
      alert.showAndWait();
    }
  }
}


