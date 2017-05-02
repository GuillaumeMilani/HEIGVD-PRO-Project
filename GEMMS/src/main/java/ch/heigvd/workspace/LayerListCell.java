package ch.heigvd.workspace;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author mathieu
 */
public class LayerListCell extends ListCell<Node> {

   @Override
   public void updateItem(Node item, boolean empty) {
      super.updateItem(item, empty);

      // If the item exists, add a layerCell
      if (empty || item == null) {
         setText(null);
         setGraphic(null);
     } else {
         LayerCell layerCell = new LayerCell(item);
         setGraphic(layerCell);
     }
   }
   
   private class LayerCell extends HBox {

      public LayerCell(Node item) {

         // Create a thumbnail for estetic purposes
         Rectangle rect = new Rectangle(40, 40);
         rect.setFill(Color.web("#bbb"));

         // Add it to the LayerCell
         getChildren().add(rect);

         // Add a Label (To be replaced by the name of the type of Node ?)
         getChildren().add(new Text("Layer"));

         // Align everything
         setAlignment(Pos.CENTER_LEFT);
         setSpacing(10);
      }
   }
}
