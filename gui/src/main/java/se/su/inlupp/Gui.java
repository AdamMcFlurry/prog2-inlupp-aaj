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
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

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
}


