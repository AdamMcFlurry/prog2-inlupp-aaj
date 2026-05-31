package se.su.inlupp;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

public class GuiNode extends BorderPane {
    private final int GUI_NODE_XCOR = 10;
    private final int GUI_NODE_YCOR = 10;
    private final int GUI_NODE_RAD = 20;
    private final Color GUI_NODE_COLOR = Color.YELLOW;

    private double startX, startY;
    private final String nodeName;

    public GuiNode(double x, double y, String nodeName) {
        relocate(x, y);
        this.nodeName = nodeName;

        VBox titlebar = new VBox();
        titlebar.getChildren().add(new Text(this.nodeName));
        
        Circle nodeCircle = new Circle(GUI_NODE_XCOR, GUI_NODE_YCOR, GUI_NODE_RAD);
        nodeCircle.setFill(GUI_NODE_COLOR);

        setTop(titlebar);
        setBottom(nodeCircle);
        setPrefSize(40, 40);
        titlebar.setPrefSize(40, 20);

        setOnMousePressed(new StartDragHandler());
        setOnMouseDragged(new DragHandler());
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
}
