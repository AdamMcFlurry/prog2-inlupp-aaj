package se.su.inlupp;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Gui extends Application {

    private Pane nodeArea;
    private TextField searchField;
    private Graph<String> graph = new ListGraph<String>();
    private ListView<String> listView;
    private Button addButton;

    public void start(Stage stage) {
        BorderPane root = new BorderPane();

        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("Route");
        menuBar.getMenus().add(fileMenu);

        MenuItem newItem = new MenuItem("New Route");
        fileMenu.getItems().add(newItem);

        MenuItem saveItem = new MenuItem("Save Route");
        fileMenu.getItems().add(saveItem);

        MenuItem loadItem = new MenuItem("Load Route");
        fileMenu.getItems().add(loadItem);

        MenuItem exitItem = new MenuItem("Exit");
        fileMenu.getItems().add(exitItem);

        root.setTop(menuBar);
        
        listView = new ListView<>();
        listView.setPrefWidth(150);
        ObservableList<String> nodeList = FXCollections.observableArrayList(graph.getNodes());
        FXCollections.sort(nodeList);
        listView.setItems(nodeList);
        
        FlowPane nodeControls = new FlowPane();
        // nodeControls.setAlignment(Pos.CENTER);
        nodeControls.setPadding(new Insets(5));
        nodeControls.setHgap(5);

        searchField = new TextField();
        Button searchButton = new Button("Search");
        addButton = new Button("Add Node");
        addButton.setOnAction(new AddHandler());
        Button deleteButton = new Button("Delete Node");
        nodeControls.getChildren().addAll(searchField, searchButton, addButton, deleteButton);

        nodeArea = new Pane();
        nodeArea.getChildren().add(nodeControls);
        
        root.setLeft(listView);
        root.setCenter(nodeArea);
        // root.setCenter(nodeControls);
        Scene scene = new Scene(root, 640, 480);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    class AddHandler implements EventHandler<ActionEvent> {
        public void handle(ActionEvent event) {
            nodeArea.setOnMouseClicked(new ClickHandler());
            nodeArea.setCursor(Cursor.CROSSHAIR);
            String word = searchField.getText();
            graph.add(word);
            
            int index = Collections.binarySearch((listView.getItems()), word);
            if (index < 0) {
                listView.getItems().add(-index - 1, word);
            }
        }
    }

    class NewButtonHandler implements EventHandler<ActionEvent> {
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

            Node node = new Node(x, y, searchField.getText());
            nodeArea.getChildren().add(node);

            nodeArea.setCursor(Cursor.DEFAULT);

            addButton.setDisable(false);
            nodeArea.setOnMouseClicked(null);
        }
    }

    // class SaveButtonHandler implements EventHandler<ActionEvent> {
    //     @Override
    //     public void handle(ActionEvent event) {
    //         try {
    //             WritableImage image = nodeArea.snapshot(null,null);
    //             BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
    //             ImageIO.write(bufferedImage, "png", new File("capture.png"));
    //         } catch (IOException e) {
    //             Alert alert = new Alert(Alert.AlertType.ERROR, "IO Error");
    //             alert.showAndWait();
    //         }
    //     }
    // }
}


