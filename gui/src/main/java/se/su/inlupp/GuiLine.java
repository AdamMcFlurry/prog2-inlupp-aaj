package se.su.inlupp;

// PROG2 VT2026, Inlämningsuppgift, del 1
// Adam McCarthy - admc0801
// Joakim Lindé - joli3174
// Arvid Flodin - arfl0534

import javafx.scene.shape.Line;

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
