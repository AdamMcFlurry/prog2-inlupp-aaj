package se.su.inlupp;

import javafx.scene.shape.Line;

public class GuiLine extends Line {
    private final Edge<String> lineEdge;

    public GuiLine(GuiNode node1, GuiNode node2, Edge<String> lineEdge) {
      this.lineEdge = lineEdge;
    
      setStartX(node1.getLayoutX());
      setStartY(node1.getLayoutY());
      setEndX(node2.getLayoutX());
      setEndY(node2.getLayoutY());
      startXProperty().bind(node1.layoutXProperty());
      startYProperty().bind(node1.layoutYProperty());
      endXProperty().bind(node2.layoutXProperty());
      endYProperty().bind(node2.layoutYProperty());
    }

    public Edge<String> getLineEdge() {
      return lineEdge;
    }

  }
