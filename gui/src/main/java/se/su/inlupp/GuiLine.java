package se.su.inlupp;

import javafx.scene.shape.Line;

//behövs förmodligen inte
public class GuiLine extends Line {
    public GuiLine(GuiNode node1, GuiNode node2) {
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

  }
