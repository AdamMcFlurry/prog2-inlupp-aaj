package se.su.inlupp;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import javax.imageio.ImageIO;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

public class Gui extends Application {

  private Pane nodeArea;
  private TextField searchField;
  private Graph<String> graph = new ListGraph<>();
  private ListView<String> listView;
  private Button addButton;
  private BorderPane root;
  // private Button addConnectionButton;
  private String imagePath;
  private boolean unsavedChanges = false;
  private TextField input1;
  private TextField input2;
  private final Map<Edge<String>, GuiEdgeLine> lineList = new HashMap<>();
  private FlowPane nodeControls;

  @Override
  public void start(Stage stage) {
    MenuBar menuBar = new MenuBar();
    Menu fileMenu = new Menu("Route");
    menuBar.getMenus().add(fileMenu);
    root = new BorderPane();

    MenuItem newItem = new MenuItem("New Route");
    newItem.setOnAction(new NewHandler());
    fileMenu.getItems().add(newItem);

    MenuItem saveItem = new MenuItem("Save Route");
    saveItem.setOnAction(new SaveHandler());
    fileMenu.getItems().add(saveItem);

    MenuItem loadItem = new MenuItem("Load Route");
    loadItem.setOnAction(new LoadHandler());
    fileMenu.getItems().add(loadItem);

    MenuItem exitItem = new MenuItem("Exit");
    exitItem.setOnAction(new ExitHandler());
    fileMenu.getItems().add(exitItem);

    root.setTop(menuBar);

    FlowPane frånTill = new FlowPane(); // ARFkod
    frånTill.setHgap(10);
    input1 = new TextField();
    input1.setPromptText("Startnod");
    input1.setStyle("-fx-border-color: black");

    input2 = new TextField();
    input2.setPromptText("Slutnod");
    input2.setStyle("-fx-border-color: black");

    Button addConnectionButton = new Button("Add Connection");
    addConnectionButton.setOnAction(new AddConnectionHandler());
    // addConnectionButton.setDisable(true);

    Button findPathButton = new Button("Find Path");
    findPathButton.setOnAction(new FindPathHandler());

    Button searchPatternButton = new Button("Switch search pattern");

    Label pil = new Label(" --> ");

    frånTill.getChildren().addAll(input1, pil, input2, findPathButton, addConnectionButton, searchPatternButton);
    frånTill.setAlignment(Pos.TOP_RIGHT);

    VBox frånTillBox = new VBox();
    frånTillBox.getChildren().addAll(menuBar, frånTill);
    root.setBottom(frånTillBox);// Slut på ARFkod

    listView = new ListView<>();
    listView.setPrefWidth(150);
    ObservableList<String> nodeList = FXCollections.observableArrayList(graph.getNodes());
    FXCollections.sort(nodeList);
    listView.setItems(nodeList);

    nodeControls = new FlowPane();
    // nodeControls.setAlignment(Pos.CENTER);
    nodeControls.setPadding(new Insets(5));
    nodeControls.setHgap(5);

    searchField = new TextField();
    Button searchButton = new Button("Search");
    addButton = new Button("Add Node");
    addButton.setOnAction(new AddHandler());
    Button deleteButton = new Button("Delete Node");
    deleteButton.setOnAction(new DeleteHandler());
    nodeControls.getChildren().addAll(searchField, searchButton, addButton, deleteButton);

    nodeArea = new Pane();
    nodeArea.getChildren().add(nodeControls);

    root.setLeft(listView);
    root.setCenter(nodeArea);
    // root.setCenter(nodeControls);

    listView.getSelectionModel().selectedItemProperty()
        .addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
          addConnectionButton.setDisable(false);
        });

    nodeArea = new Pane();
    nodeArea.getChildren().add(nodeControls);

    root.setLeft(listView);
    root.setCenter(nodeArea);
    // root.setCenter(nodeControls);
    Scene scene = new Scene(root, 740, 580);
    stage.setScene(scene);

    stage.setOnCloseRequest(event -> {
      if (!confirmUnsavedChanges()) {
        event.consume();
      }
    });

    stage.setScene(scene);
    stage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }

  class AddHandler implements EventHandler<ActionEvent> {
    @Override
    public void handle(ActionEvent event) {
      nodeArea.setOnMouseClicked(new ClickHandler());
      nodeArea.setCursor(Cursor.CROSSHAIR);
      unsavedChanges = true;
    }
  }

  class AddConnectionHandler implements EventHandler<ActionEvent> {
    @Override
    public void handle(ActionEvent event) {
      String node1 = input1.getText();
      input1.clear();
      String node2 = input2.getText();
      input2.clear();
      TextInputDialog tiDialog = new TextInputDialog();
      tiDialog.setTitle("Connection Weight Input");
      tiDialog.setHeaderText("Enter the weight of the dialog: ");
      tiDialog.setContentText("Weight: ");

      Optional<String> result = tiDialog.showAndWait();
      if (result.isPresent()) {
        graph.connect(node1, node2, (node1 + " till " + node2), Integer.parseInt(result.get()));
        createNewLine(getNodeByName(node1), getNodeByName(node2), graph.getEdgeBetween(node1, node2));
      }
    }
  }

  private void createNewLine(Node node1, Node node2, Edge<String> edge) {
    GuiEdgeLine newLine = new GuiEdgeLine(edge);
    lineList.put(edge, newLine);
    newLine.setStartX(node1.getX());
    newLine.setStartY(node1.getY());
    newLine.setEndX(node2.getX());
    newLine.setEndY(node2.getY());
    newLine.startXProperty().bind(node1.layoutXProperty());
    newLine.startYProperty().bind(node1.layoutYProperty());
    newLine.endXProperty().bind(node2.layoutXProperty());
    newLine.endYProperty().bind(node2.layoutYProperty());
    nodeArea.getChildren().add(newLine);
  }

  private class GuiEdgeLine extends Line {
    private final Edge<String> lineEdge;

    public GuiEdgeLine(Edge<String> lineEdge) {
      this.lineEdge = lineEdge;
    }

    public Edge<String> getLineEdge() {
      return lineEdge;
    }
  }

  private Node getNodeByName(String nodeName) {
    for (Object node : nodeArea.getChildren()) {
      if (node instanceof Node) {
        if (((Node) node).getNodeName().equals(nodeName)) {
          return (Node) node;
        }
      }
    }
    return null;
  }

  class NewButtonHandler implements EventHandler<ActionEvent> {
    @Override
    public void handle(ActionEvent event) {
      nodeArea.setOnMouseClicked(new ClickHandler());

      nodeArea.setCursor(Cursor.CROSSHAIR);

      addButton.setDisable(true);
    }
  }

  class FindPathHandler implements EventHandler<ActionEvent> {
    @Override
    public void handle(ActionEvent event) {
      PathFinder<String> bfsPathFinder = new BFSPathFinder<>();
      Path<String> path = bfsPathFinder.findPath(graph, input1.getText(), input2.getText());
      input1.clear();
      input2.clear();
      int totalWeight = 0;
      for (GuiEdgeLine edgeLine : lineList.values()) {
        edgeLine.setStyle("-fx-stroke: black;");
      }

      for (Edge<String> edge : path.getEdges()) {
        for (GuiEdgeLine edgeLine : lineList.values()) {
          if (edgeLine.getLineEdge().equals(edge)) {
            edgeLine.setStyle("-fx-stroke: red;");
            totalWeight += edge.getWeight();

          }
        }
      }

      Alert alert = new Alert(Alert.AlertType.INFORMATION, "The total weight of the path is " + totalWeight);
      alert.showAndWait();
    }
  }

  class ClickHandler implements EventHandler<MouseEvent> {
    public void handle(MouseEvent event) {
      double x = event.getX();
      double y = event.getY();

      String word = searchField.getText();
      graph.add(word);

      int index = Collections.binarySearch((listView.getItems()), word);
      if (index < 0) {
        listView.getItems().add(-index - 1, word);
      }

      Node node = new Node(x, y, searchField.getText());
      searchField.clear();
      nodeArea.getChildren().add(node);

      nodeArea.setCursor(Cursor.DEFAULT);

      addButton.setDisable(false);
      nodeArea.setOnMouseClicked(null);
    }
  }

  class DeleteHandler implements EventHandler<ActionEvent> {
    public void handle(ActionEvent event) {
      String selectedNode = listView.getSelectionModel().getSelectedItem();

      if (selectedNode == null) {
        Alert alert = new Alert(Alert.AlertType.ERROR, "No node selected.");
        alert.showAndWait();
        return;
      }
      try {
        graph.getEdgesFrom(selectedNode).stream().forEach((e)->nodeArea.getChildren().remove(lineList.remove(e)));
        
        graph.remove(selectedNode);
        listView.getItems().remove(selectedNode);
        nodeArea.getChildren().remove(getNodeByName(selectedNode));
        
        unsavedChanges = true;

      } catch (NoSuchElementException e) {
        Alert alert = new Alert(Alert.AlertType.ERROR, "Node does not exist.");
        alert.showAndWait();
      }
    }
  }

  class NewHandler implements EventHandler<ActionEvent> {
    public void handle(ActionEvent event) {
      if (confirmUnsavedChanges()) {
        graph = new ListGraph<>();
        AddDefaultStations();

        unsavedChanges = false;
      }
    }
  }

  class SaveHandler implements EventHandler<ActionEvent> {
    public void handle(ActionEvent event) {
      // F6, F9
      // Sparas både som textfil (kan laddas up och manipuleras) och som .png (kan
      // visas men inte manipuleras) Hittade i F14
      // Hur ska vi göra med att välja antingen PNG eller TXT?
      savePNG();
      saveTXT();
    }
  }

  class LoadHandler implements EventHandler<ActionEvent> {
    public void handle(ActionEvent event) {
      // F6, F8, F9
      // Ladda upp. Både som textfil (manipuleras) och som .png (inte manipuleras)
      // Hur ska vi göra med att välja antingen PNG eller TXT?
      if (confirmUnsavedChanges()) {
        loadTXT();
        loadPNG();
      }
    }
  }

  class ExitHandler implements EventHandler<ActionEvent> {
    public void handle(ActionEvent event) {
      if (confirmUnsavedChanges()) {
        Platform.exit();
      }
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

      writer.println("IMAGE:" + imagePath);

      for (String node : graph.getNodes()) {
        Node visualNode = getNodeByName(node);
        writer.println("NODE:" + node + ":" + visualNode.getX() + ":" + visualNode.getY());
      }
      for (String node : graph.getNodes()) {
        for (Edge<String> edge : graph.getEdgesFrom(node)) {
          writer.println("EDGE:" + node + ":" + edge.getDestination() + ":" + edge.getName() + ":" + edge.getWeight());
        }
      }
      writer.close();
      unsavedChanges = false;

    } catch (Exception e) {
      Alert alert = new Alert(Alert.AlertType.ERROR, "Could not save route.");
      alert.showAndWait();
      unsavedChanges = true;

    }
  }

  private void savePNG() {
    try {
      File file = new File("route.png");
      WritableImage image = nodeArea.snapshot(null, null);
      BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
      ImageIO.write(bufferedImage, "png", file);

      imagePath = file.getAbsolutePath();
      unsavedChanges = false;
    } catch (IOException e) {
      Alert alert = new Alert(Alert.AlertType.ERROR, "Could not save PNG");
      alert.showAndWait();
      unsavedChanges = true;
    }
  }

  private void loadTXT() {
    try {
      graph = new ListGraph<>();
      nodeArea.getChildren().clear();
      nodeArea.getChildren().add(nodeControls);
      FileReader fileReader = new FileReader("route.txt");
      BufferedReader reader = new BufferedReader(fileReader);
      List<String[]> edgeList = new ArrayList<>();

      String line;

      while ((line = reader.readLine()) != null) {
        if (line.startsWith("IMAGE:")) {
          imagePath = line.substring(6);
        } else if (line.startsWith("NODE:")) {
          String[] parts = line.split(":");
          String nodeName = parts[1];
          double x = Double.parseDouble(parts[2]);
          double y = Double.parseDouble(parts[3]);
          graph.add(nodeName);
          Node visualNode = new Node(x, y, nodeName);
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

        Node startNode = getNodeByName(from);
        Node endNode = getNodeByName(to);

        Edge<String> guiEdge = graph.getEdgeBetween(from, to);
        createNewLine(startNode, endNode, guiEdge);

        ObservableList<String> updatedList = FXCollections.observableArrayList(graph.getNodes());
        FXCollections.sort(updatedList);
        listView.setItems(updatedList);
      }
    } catch (FileNotFoundException e) {
      Alert alert = new Alert(Alert.AlertType.ERROR, "route.txt not found");
      alert.showAndWait();
    } catch (IOException e) {
      Alert alert = new Alert(Alert.AlertType.ERROR, "Could not read file.");
      alert.showAndWait();
    } catch (Exception e) {
      Alert alert = new Alert(Alert.AlertType.ERROR, "Could not load route.");
      alert.showAndWait();
    }
  }

  private void loadPNG() {
    try {
      if (imagePath == null || imagePath.isEmpty()) {
        Alert alert = new Alert(Alert.AlertType.ERROR, "No image path saved.");
        alert.showAndWait();
        return;
      }
      File file = new File(imagePath);
      if (!file.exists()) {
        Alert alert = new Alert(Alert.AlertType.ERROR, "Image file not found.");
        alert.showAndWait();
        return;
      }
      Image image = new Image(file.toURI().toString());
      ImageView imageView = new ImageView(image);
      imageView.setPreserveRatio(true);
      imageView.setFitWidth(800);
      imageView.setFitHeight(600);

      Pane imagePane = new Pane(imageView);
      Scene imageScene = new Scene(imagePane);
      Stage imageStage = new Stage();
      imageStage.setTitle("Loaded Route Image");
      imageStage.setScene(imageScene);
      imageStage.show();
    } catch (Exception e) {
      Alert alert = new Alert(Alert.AlertType.ERROR, "Could not load PNG.");
      alert.showAndWait();
    }
  }

  private boolean confirmUnsavedChanges() {
    if (!unsavedChanges) {
      return true;
    }
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Unsaved changes");
    alert.setHeaderText("You have unsaved changes.");
    alert.setContentText("Are you sure you want to continue?");

    Optional<ButtonType> result = alert.showAndWait();

    return result.isPresent() && result.get() == ButtonType.OK;
  }
}
