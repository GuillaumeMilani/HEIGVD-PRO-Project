/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.heigvd.workspace;

import ch.heigvd.gemms.Constants;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 *
 * @author mathieu
 */
public class Workspace extends AnchorPane {

   private final LayerList layerList;
   private VBox layersController;

   /**
    * Constructor for a new instance of Workspace. The Workspace extends a Pane
    * which represents the working area of the document. It sets its initial
    * position at the center of the containing pane.
    *
    * @param width the width of the Workspace (according to the document needs)
    * @param height the height of the Workspace (according to the document
    * needs)
    * @param pane the application pane in which the Workspace is to be added.
    */
   public Workspace(int width, int height, Pane pane) {
      // Explciit call to parent constructor
      super();

      layerList = new LayerList(getChildren());
      // Set the workspace Pane position to be at the center of pane
      int posX = (int) ((pane.getPrefWidth() - width) / 2);
      int posY = (int) ((pane.getPrefHeight() - height) / 2);
      
      setLayoutX(posX);
      setLayoutY(posY);

      // Set preferredSize
      setPrefSize(width, height);
      setMaxSize(width, height);
      setMinSize(width, height);
      
      // Give the CSS ID
      setId("workspacePane");

      // Register scroll event for zoom
      pane.setOnScroll(new EventHandler<ScrollEvent>() {
         @Override
         public void handle(ScrollEvent event) {
            if (event.isControlDown()) {
               if (event.getDeltaY() > 0) {
                  zoom(1.05);
               } else {
                  zoom(0.95);
               }
               event.consume();
            }
         }
      });

      EventHandler dragEventHandler = new EventHandler<MouseEvent>() {
         private double x;
         private double y;

         @Override
         public void handle(MouseEvent event) {
            if (event.isShiftDown()) {
               if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
                  x = event.getX();
                  y = event.getY();
               } else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                  move(event.getX() - x, event.getY() - y);
                  x = event.getX();
                  y = event.getY();
               }
               event.consume();
            }
         }
      };

      pane.addEventHandler(MouseEvent.ANY, dragEventHandler);

   }

   public List<Node> getCurrentLayers() {
      return layerList.getSelectionModel().getSelectedItems();
   }

   /**
    *
    * @param node
    */
   public void addLayer(Node node) {
      layerList.getItems().add(0, node);
      layerList.getSelectionModel().clearSelection();
      layerList.getSelectionModel().selectFirst();
   }

   /**
    *
    * @param node
    */
   public void removeLayer(Node node) {
      layerList.getItems().remove(node);
   }

   /**
    *
    * @return
    */
   public List<Node> getLayers() {
      return layerList.getItems();
   }

   /**
    *
    * @param factor
    */
   public void zoom(double factor) {
      setScaleX(getScaleX() * factor);
      setScaleY(getScaleY() * factor);
   }

   /**
    * Translates the Workspace Pane container by a (x, y) translation vector.
    *
    * @param x the x coordinate of the translation
    * @param y the y coordinate of the translation
    */
   public void move(double x, double y) {
      setTranslateX(getTranslateX() + x);
      setTranslateY(getTranslateY() + y);
   }

   /**
    *
    */
   public void crop() {

   }

   public VBox getWorkspaceController() {
      if (layersController == null) {

         // Create the controller instance
         layersController = new VBox();

         // Add the LayerList
         layersController.getChildren().add(layerList);

         // Add a button to delete Layers
         Button delete = new Button("X");
         delete.setPrefSize(Constants.BUTTONS_HEIGHT, Constants.BUTTONS_HEIGHT);
         delete.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
               List<Node> items = getCurrentLayers();
               layerList.getItems().removeAll(items);
            }
         });
         layersController.getChildren().add(delete);

         return layersController;
      } else {
         return layersController;
      }
   }

}
