/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.heigvd.workspace;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.util.Callback;

/**
 *
 * @author mathieu
 */

/**
 * LayerList is the class who's in charge of controlling the Workspace
 * children. It extends ListView, thus allowing to select, control and safely
 * delete Nodes from the workspace children, while keeping a coherent state 
 * in the controller.
 */
public class LayerList extends ListView {

   public LayerList(ObservableList<Node> layers) {
      super(layers);
        
      // Set the CellFactory for this ListView
      setCellFactory(new Callback<ListView<Node>, ListCell<Node>>() {
         @Override
         public ListCell<Node> call(ListView<Node> list) {
            return new LayerListCell();
         }
      }
      );

      // Allow multiple selection for this ListView
      getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
   }
}
