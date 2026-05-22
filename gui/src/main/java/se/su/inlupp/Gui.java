package se.su.inlupp;

import java.util.Collections;

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

public class Gui extends Application {

    private TextField searchField;
    private Graph<String> graph = new ListGraph<String>();
    ListView<String> listView;

public void start(Stage stage) {
    graph.add("Stockholm");
    graph.add("Oslo");
    graph.add("Köpenhamn");
    graph.add("Kiruna");

    BorderPane root = new BorderPane();

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
    public void handle(ActionEvent event) {
      System.out.println("New Route selected");
      // Vill skapa en ny route från två nya noder, samma graph, ej ny graph.
    }
  }
  class SaveHandler implements EventHandler<ActionEvent>{
    public void handle(ActionEvent event) {
      System.out.println("Save Route selected");
      // Vill spara route:n så att den kan kommas åt vid senare tillfälle, tillåt modifikation av sparad route?
    }
  }
  class LoadHandler implements EventHandler<ActionEvent>{
    public void handle(ActionEvent event) {
      System.out.println("Load Route selected");
      // Vill komma åt tidigare sparad route
    }
  }
  class ExitHandler implements EventHandler<ActionEvent>{
    public void handle(ActionEvent event) {
      Platform.exit();
    }
  }
}


