package se.su.inlupp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Gui extends Application {

  private Pane nodeArea;
  private TextField searchField;
  private GraphModel graphModel = new GraphModel();
  private ListView<String> listView;
  private Button addButton;
  private BorderPane root;
  private Button addConnectionButton;
  private String imagePath;
  private boolean unsavedChanges = false;
  private TextField input1;
  private TextField input2;
  private FlowPane nodeControls;
  private ImageView backgroundImageView;
  private PFType currentPFAlgorithm = PFType.BFS;
  private Button searchPatternButton;

  private Map<Edge<String>, GuiLine> edgeGuiLineMap = new HashMap<>();
  private Map<String, GuiNode> nodeMap = new HashMap<>();

  @Override
  public void start(Stage stage) {
    root = new BorderPane();

    createMenuBar();
    createBottomControls();
    createListView();
    createNodeControls();
    createNodeArea();

    Scene scene = new Scene(root, 740, 580);

    setupStage(stage, scene);

    stage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }

  private void createMenuBar() {
    MenuBar menuBar = new MenuBar();
    Menu fileMenu = new Menu("Route");

    MenuItem newItem = new MenuItem("New Route");
    newItem.setOnAction(new NewHandler());

    MenuItem saveItem = new MenuItem("Save Route");
    saveItem.setOnAction((e) -> {
      savePNG();
      saveTXT();
    });

    MenuItem loadItem = new MenuItem("Load Route");
    loadItem.setOnAction((e) -> {
      if (confirmUnsavedChanges()) {
        loadTXT();
        loadPNG();
      }
    });

    MenuItem exitItem = new MenuItem("Exit");
    exitItem.setOnAction((e) -> {
      if (confirmUnsavedChanges()) {
        Platform.exit();
      }
    });

    fileMenu.getItems().addAll(newItem, saveItem, loadItem, exitItem);

    menuBar.getMenus().add(fileMenu);
    root.setTop(menuBar);
  }

  private void createBottomControls() {
    FlowPane fromToPane = new FlowPane();
    fromToPane.setHgap(10);

    input1 = new TextField();
    input1.setPromptText("Start node");
    input1.setStyle("-fx-border-color: black");

    input2 = new TextField();
    input2.setPromptText("End node");
    input2.setStyle("-fx-border-color: black");

    addConnectionButton = new Button("Add Connection");
    addConnectionButton.setOnAction(new AddConnectionHandler());

    Button findPathButton = new Button("Find Path");
    findPathButton.setOnAction(new FindPathHandler());

    searchPatternButton = new Button("Switch to DFS");
    searchPatternButton.setOnAction(new SwitchSearchPatternHandler());

    Label arrow = new Label("-->");

    fromToPane.getChildren().addAll(input1, arrow, input2, findPathButton, addConnectionButton, searchPatternButton);

    VBox bottomBox = new VBox();
    bottomBox.getChildren().add(fromToPane);
    root.setBottom(bottomBox);
  }

  private void createListView() {
    listView = new ListView<>();
    listView.setPrefWidth(150);
    ObservableList<String> nodeList = FXCollections.observableArrayList(graphModel.getGraphNodes());
    FXCollections.sort(nodeList);
    listView.setItems(nodeList);
    root.setLeft(listView);
  }

  private void createNodeControls() {
    nodeControls = new FlowPane();

    nodeControls.setPadding(new Insets(5));
    nodeControls.setHgap(5);

    searchField = new TextField();

    addButton = new Button("Add Node");
    addButton.setOnAction(new AddHandler());

    Button deleteButton = new Button("Delete Node");
    deleteButton.setOnAction(new DeleteHandler());

    Button loadImageButton = new Button("Load Image");
    loadImageButton.setOnAction(new LoadImageHandler());

    nodeControls.getChildren().addAll(searchField, addButton, deleteButton, loadImageButton);
  }

  private void createNodeArea() {
    nodeArea = new Pane();
    nodeArea.getChildren().add(nodeControls);
    root.setCenter(nodeArea);
  }

  private void setupStage(Stage stage, Scene scene) {
    stage.setScene(scene);
    stage.setOnCloseRequest(event -> {
      if (!confirmUnsavedChanges()) {
        event.consume();
      }
    });
  }

  private void saveTXT() {
    try {
      RouteFileManager.saveTXT(graphModel, nodeMap, imagePath);
      unsavedChanges = false;

    } catch (Exception e) {
      Alert alert = new Alert(Alert.AlertType.ERROR, "Could not save route.");
      alert.showAndWait();
      unsavedChanges = true;

    }
  }

  private void savePNG() {
    try {
      RouteFileManager.savePNG(nodeArea);
      imagePath = new File("route.png").getAbsolutePath();

      unsavedChanges = false;
    } catch (IOException e) {
      Alert alert = new Alert(Alert.AlertType.ERROR, "Could not save PNG");
      alert.showAndWait();
      unsavedChanges = true;
    }
  }

  private void loadTXT() {
    try {
      graphModel.clearGraph();
      nodeArea.getChildren().clear();
      nodeMap.clear();
      edgeGuiLineMap.clear();

      nodeArea.getChildren().add(nodeControls);

      RouteData routeData = RouteFileManager.loadTXT(graphModel);

      imagePath = routeData.getImagePath();

      for (Map.Entry<String, double[]> entry : routeData.getNodes().entrySet()) {
        String nodeName = entry.getKey();

        double x = entry.getValue()[0];
        double y = entry.getValue()[1];

        GuiNode guiNode = new GuiNode(x, y, nodeName);
        nodeMap.put(nodeName, guiNode);
        nodeArea.getChildren().add(guiNode);
      }

      for (String[] edge : routeData.getEdges()) {
        String from = edge[1];
        String to = edge[2];

        GuiNode startNode = nodeMap.get(from);
        GuiNode endNode = nodeMap.get(to);

        GuiLine guiLine = new GuiLine(startNode, endNode);

        Edge<String> graphEdge = graphModel.getGraph().getEdgeBetween(from, to);
        edgeGuiLineMap.put(graphEdge, guiLine);
        nodeArea.getChildren().add(guiLine);
      }

      ObservableList<String> updatedList = FXCollections.observableArrayList(graphModel.getGraphNodes());
      FXCollections.sort(updatedList);
      listView.setItems(updatedList);

      unsavedChanges = false;

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
        return;
      }
      File file = new File(imagePath);
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
      String node2 = input2.getText();
      input1.clear();
      input2.clear();

      TextInputDialog tiDialog = new TextInputDialog();
      tiDialog.setTitle("Connection Weight Input");
      tiDialog.setHeaderText("Enter the weight of the dialog: ");
      tiDialog.setContentText("Weight: ");

      Optional<String> result = tiDialog.showAndWait();
      int edgeWeight = Integer.parseInt(result.get());

      if (result.isPresent()) {
        GuiLine newLine = new GuiLine(nodeMap.get(node1), nodeMap.get(node2));
        graphModel.connectNodes(node1, node2, edgeWeight, edgeGuiLineMap, newLine);

        nodeArea.getChildren().add(newLine);
      }
      unsavedChanges = true;
    }
  }

  class NewButtonHandler implements EventHandler<ActionEvent> {
    @Override
    public void handle(ActionEvent event) {
      nodeArea.setOnMouseClicked(new ClickHandler());

      nodeArea.setCursor(Cursor.CROSSHAIR);

      addButton.setDisable(true);
    }
  }

  class LoadImageHandler implements EventHandler<ActionEvent> {
    public void handle(ActionEvent event) {
      FileChooser fileChooser = new FileChooser();
      fileChooser.setTitle("Open Image");
      fileChooser.getExtensionFilters()
          .addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));

      File selectedFile = fileChooser.showOpenDialog(null);
      if (selectedFile != null) {
        imagePath = selectedFile.getAbsolutePath();
        Image image = new Image(selectedFile.toURI().toString());

        backgroundImageView = new ImageView(image);
        backgroundImageView.setPreserveRatio(true);
        backgroundImageView.fitWidthProperty().bind(nodeArea.widthProperty());
        backgroundImageView.fitHeightProperty().bind(nodeArea.heightProperty());
        backgroundImageView.setMouseTransparent(true);

        nodeArea.getChildren().remove(backgroundImageView);
        nodeArea.getChildren().add(0, backgroundImageView);

        unsavedChanges = true;

      }
    }
  }

  class FindPathHandler implements EventHandler<ActionEvent> {
    @Override
    public void handle(ActionEvent event) {
      Path<String> path = graphModel.getPath(input1.getText(), input2.getText());
      input1.clear();
      input2.clear();

      for (GuiLine edgeLine : edgeGuiLineMap.values()) {
        edgeLine.setStyle("-fx-stroke: black;");
      }

      for (Edge<String> edge : path.getEdges()) {
        for (GuiLine edgeLine : edgeGuiLineMap.values()) {
          if (edgeGuiLineMap.get(edge).equals(edgeLine)) {
            edgeLine.setStyle("-fx-stroke: red;");
          }
        }
      }

      Alert alert = new Alert(Alert.AlertType.INFORMATION, "The total weight of the path is " + path.getTotalWeight());
      alert.showAndWait();
    }
  }

  class SwitchSearchPatternHandler implements EventHandler<ActionEvent> {
    @Override
    public void handle(ActionEvent event) {
      switch (currentPFAlgorithm) {
        case BFS:
          graphModel.setPathFinder(new DFSPathFinder<>());
          currentPFAlgorithm = PFType.DFS;
          searchPatternButton.setText("Switch to BFS");
          break;
        case DFS:
          graphModel.setPathFinder(new BFSPathFinder<>());
          currentPFAlgorithm = PFType.BFS;
          searchPatternButton.setText("Switch to DFS");
          break;
      }
    }
  }

  class ClickHandler implements EventHandler<MouseEvent> {
    public void handle(MouseEvent event) {
      double x = event.getX();
      double y = event.getY();

      String nodeName = searchField.getText();
      graphModel.addNode(nodeName);

      int index = Collections.binarySearch((listView.getItems()), nodeName);
      if (index < 0) {
        listView.getItems().add(-index - 1, nodeName);
      }

      GuiNode node = new GuiNode(x, y, nodeName);
      nodeMap.put(nodeName, node);
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
        for (Edge<String> edge : graphModel.deleteNode(selectedNode)) {
          nodeArea.getChildren().removeAll(edgeGuiLineMap.remove(edge));
        }

        listView.getItems().remove(selectedNode);
        nodeArea.getChildren().remove(nodeMap.get(selectedNode));

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
        graphModel.clearGraph();
        nodeArea.getChildren().clear();
        nodeArea.getChildren().add(nodeControls);
        listView.getItems().clear();

        unsavedChanges = false;
      }
    }
  }
}
