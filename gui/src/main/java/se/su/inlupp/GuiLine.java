package se.su.inlupp;

import javafx.scene.shape.Line;

//necessary?
public class GuiLine extends Line {
    private final Edge<String> lineEdge;

    public GuiLine(GuiNode node1, GuiNode node2, Edge<String> lineEdge) {
      this.lineEdge = lineEdge;
      
      double layoutX = node1.getLayoutX();
      double layoutY = node1.getLayoutY();

      setStartX(layoutX);
      setStartY(layoutY);
      
      setEndX(node2.getLayoutY());
      setEndY(node2.getLayoutY());

      startXProperty().bind(node1.layoutXProperty());
      startYProperty().bind(node1.layoutYProperty());
      
      endXProperty().bind(node2.layoutXProperty());
      endYProperty().bind(node2.layoutYProperty());
    }

    //remove
    public Edge<String> getLineEdge() {
      return lineEdge;
    }

  }
