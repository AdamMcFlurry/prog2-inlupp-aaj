package se.su.inlupp;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Gui extends Application {

  public void start(Stage stage) {
    Graph<String> graph = new ListGraph<String>();
    String javaVersion = System.getProperty("java.version");
    String javafxVersion = System.getProperty("javafx.version");
    Label label =
        new Label("Hello, JavaFX " + javafxVersion + ", running on Java " + javaVersion + ".");

    VBox root = new VBox(30, label);
   VBox högerbar= new VBox();// min också
    högerbar.setAlignment(Pos.TOP_RIGHT);
    root.setAlignment(Pos.CENTER);

     FlowPane karta= new FlowPane();  //min kod
    
    TextField input1= new TextField();
    input1.setPromptText("Startnod");
    input1.setStyle("-fx-border-color: black");

    TextField input2= new TextField();
    input2.setPromptText("Slutnod");
    input2.setStyle("-fx-border-color: black");

    Label pil= new Label(" --> ");

    karta.getChildren().addAll(input1, pil, input2);
    karta.setAlignment( Pos.TOP_RIGHT);
     root.getChildren().add(karta);//Slut på kod

    Scene scene = new Scene(root, 640, 480);
    //Scene scene2 = new Scene(högerbar, 640, 100)// min kod å
    stage.setScene(scene);
    //stage.setScene(scene2);
    stage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
