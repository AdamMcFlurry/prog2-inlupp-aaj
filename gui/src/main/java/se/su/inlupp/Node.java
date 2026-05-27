package se.su.inlupp;

import javafx.event.EventHandler;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class Node extends BorderPane {
    double startX, startY;
    public Node(double x, double y, String nodeName) {
        relocate(x, y);
        Pane titlebar = new Pane();
        //fixa!!!!!
        titlebar.getChildren().add(new Text(nodeName));
        TextArea text = new TextArea();
        setTop(titlebar);
        setCenter(text);
        setPrefSize(40, 40);
        titlebar.setPrefSize(40, 20);
        titlebar.setBackground(Background.fill(Color.YELLOW));
        text.setStyle("-fx-font: 14px 'Courier New'; -fx-control-inner-background: #fafa82");

        setOnMousePressed(new StartDragHandler());
        setOnMouseDragged(new DragHandler());

        setOnKeyPressed(new KeyHandler());

        titlebar.setOnMouseClicked( (event) -> {
            titlebar.setBackground(Background.fill(Color.ORANGE));
            requestFocus();
        });

        focusedProperty().addListener( (obs, oldValue, newValue) -> {
            if (newValue) {
                requestFocus();
                titlebar.setBackground(Background.fill(Color.ORANGE));
            } else {
                titlebar.setBackground(Background.fill(Color.YELLOW));
            }
        });
    }

    class StartDragHandler implements EventHandler<MouseEvent> {
        public void handle(MouseEvent event) {
              startX = event.getX();
              startY = event.getY();
        }
    }

    class DragHandler implements EventHandler<MouseEvent> {

        public void handle(MouseEvent event) {
            double newX = getLayoutX() + event.getX() - startX;
            double newY = getLayoutY() + event.getY() - startY;
            relocate(newX, newY);
        }
    }

    class KeyHandler implements EventHandler<KeyEvent> {
        @Override
        public void handle(KeyEvent event) {
            double x = getLayoutX();
            double y = getLayoutY();

            switch (event.getCode()) {
                case DOWN: y += 10; break;
                case UP: y -= 10; break;
                case RIGHT: x += 10; break;
                case LEFT: x -= 10; break;
            }
            event.consume();
            relocate(x, y);
        }
    }


}
