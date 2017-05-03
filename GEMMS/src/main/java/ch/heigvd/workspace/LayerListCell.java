package ch.heigvd.workspace;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
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

/**
 * LayerListCell is a specialisation of JavaFx ListCell. The purpose is to 
 * represent a layer of the workspace in its ListView.
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
   
   /**
    * LayerCell represents the implementation of the Layer representation. 
    * Its role is to create the structure to represent an item in the 
    * workspace ListView.
    */
   private class LayerCell extends HBox {

      public LayerCell(Node item) {

         // Create a thumbnail for estetic purposes
         AnchorPane rect = new AnchorPane();
         rect.setPrefSize(40, 40);

         // Add a Label (To be replaced by the name of the type of Node ?)
         Text t = new Text("Layer");
         
         if (LayerListable.class.isInstance(item)) {
            rect.getStyleClass().add( ((LayerListable)item).getThumbnailClass() );
            t.setText( ((LayerListable)item).getLayerName() );
         }

         // Add it to the LayerCell
         getChildren().add(rect);
         getChildren().add(t);

         // Align everything
         setAlignment(Pos.CENTER_LEFT);
         setSpacing(10);
      }
   }
}
