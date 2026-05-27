package se.su.inlupp;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Scanner;

import javax.imageio.ImageIO;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
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

  @Override
  public void start(Stage stage) {
    MenuBar menuBar = new MenuBar();
    Menu fileMenu = new Menu("Route");
    menuBar.getMenus().add(fileMenu);
    root = new BorderPane();

    MenuItem newItem = new MenuItem("New Route");
    fileMenu.getItems().add(newItem);

    MenuItem saveItem = new MenuItem("Save Route");
    fileMenu.getItems().add(saveItem);

    MenuItem loadItem = new MenuItem("Load Route");
    fileMenu.getItems().add(loadItem);

    MenuItem exitItem = new MenuItem("Exit");
    fileMenu.getItems().add(exitItem);

        root.setTop(menuBar);
        
        FlowPane frånTill= new FlowPane();  //ARFkod
 
        TextField input1= new TextField();
        input1.setPromptText("Startnod");
        input1.setStyle("-fx-border-color: black");

        TextField input2= new TextField();
        input2.setPromptText("Slutnod");
        input2.setStyle("-fx-border-color: black");

        Label pil= new Label(" --> ");

        frånTill.getChildren().addAll(input1, pil, input2);
        frånTill.setAlignment( Pos.TOP_RIGHT);

        VBox frånTillBox = new VBox();
        frånTillBox.getChildren().addAll(menuBar,frånTill);
        root.setTop(frånTillBox);//Slut på ARFkod


        listView = new ListView<>();
        listView.setPrefWidth(150);
        ObservableList<String> nodeList = FXCollections.observableArrayList(graph.getNodes());
        FXCollections.sort(nodeList);
        listView.setItems(nodeList);

        //
        
        FlowPane nodeControls = new FlowPane();
        // nodeControls.setAlignment(Pos.CENTER);
        nodeControls.setPadding(new Insets(5));
        nodeControls.setHgap(5);

    searchField = new TextField();
    Button searchButton = new Button("Search");
    addButton = new Button("Add Node");
    addButton.setOnAction(new AddHandler());
    Button deleteButton = new Button("Delete Node");
    deleteButton.setOnAction(new DeleteHandler());
    Button addConnectionButton = new Button("Add Connection");
    addConnectionButton.setOnAction(new AddConnectionHandler());
    addConnectionButton.setDisable(true);
    nodeControls.getChildren().addAll(searchField, searchButton, addButton, deleteButton, addConnectionButton);

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
      Alert alert = new Alert(Alert.AlertType.INFORMATION, "Please select a second node in the list");
      alert.showAndWait();

      listView.getSelectionModel().selectedItemProperty()
          .addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            graph.connect(oldValue, newValue, (oldValue + " till " + newValue), 0);
            Line newLine = new BoundLine(new SimpleDoubleProperty(getNodeByName(oldValue).getX() + 20),
                new SimpleDoubleProperty(getNodeByName(oldValue).getY() + 40),
                new SimpleDoubleProperty(getNodeByName(newValue).getX() + 20),
                new SimpleDoubleProperty(getNodeByName(newValue).getY() + 40));
            // newLine.setStartX(getNodeByName(oldValue).getX() + 20);
            // newLine.setStartY(getNodeByName(oldValue).getY() + 40);
            // newLine.setEndX(getNodeByName(newValue).getX() + 20);
            // newLine.setEndY(getNodeByName(newValue).getY() + 40);
            nodeArea.getChildren().add(newLine);
          });
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

  // kopierad
  class BoundLine extends Line {
    BoundLine(DoubleProperty startX, DoubleProperty startY, DoubleProperty endX, DoubleProperty endY) {
      startXProperty().bind(startX);
      startYProperty().bind(startY);
      endXProperty().bind(endX);
      endYProperty().bind(endY);
      setStrokeWidth(2);
      setStroke(Color.GRAY.deriveColor(0, 1, 1, 0.5));
      setStrokeLineCap(StrokeLineCap.BUTT);
      getStrokeDashArray().setAll(10.0, 5.0);
      setMouseTransparent(true);
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

  // class SaveButtonHandler implements EventHandler<ActionEvent> {
  // @Override
  // public void handle(ActionEvent event) {
  // try {
  // WritableImage image = nodeArea.snapshot(null,null);
  // BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
  // ImageIO.write(bufferedImage, "png", new File("capture.png"));
  // } catch (IOException e) {
  // Alert alert = new Alert(Alert.AlertType.ERROR, "IO Error");
  // alert.showAndWait();
  // }
  // }
  // }

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
        writer.println("NODE:" + node);
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
      WritableImage image = root.snapshot(null, null);
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
      Scanner scanner = new Scanner(new File("route.txt"));
      List<String[]> edgeList = new ArrayList<>();

      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();
        String[] parts = line.split(":");

        if (parts[0].equals("IMAGE")) {
          imagePath = parts[1];
          Image image = new Image(new File(imagePath).toURI().toString());
          ImageView imageView = new ImageView(image);
          root.setCenter(imageView);
        } else if (line.startsWith("NODE:")) {
          String node = line.substring(5);
          graph.add(node);
        } else if (parts[0].equals("EDGE:")) {
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
