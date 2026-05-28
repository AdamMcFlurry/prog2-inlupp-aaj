package se.su.inlupp;

import javafx.scene.shape.Line;

public class GuiEdgeLine extends Line {
    private final Edge<String> lineEdge;

    public GuiEdgeLine(Edge<String> lineEdge) {
      this.lineEdge = lineEdge;
    }

    public Edge<String> getLineEdge() {
      return lineEdge;
    }

    public static GuiEdgeLine createNewLine(Node node1, Node node2, Edge<String> edge) {
    GuiEdgeLine newLine = new GuiEdgeLine(edge);
    
    newLine.setStartX(node1.getLayoutX());
    newLine.setStartY(node1.getLayoutY());
    newLine.setEndX(node2.getLayoutX());
    newLine.setEndY(node2.getLayoutY());
    newLine.startXProperty().bind(node1.layoutXProperty());
    newLine.startYProperty().bind(node1.layoutYProperty());
    newLine.endXProperty().bind(node2.layoutXProperty());
    newLine.endYProperty().bind(node2.layoutYProperty());

    return newLine;
  }
  }
