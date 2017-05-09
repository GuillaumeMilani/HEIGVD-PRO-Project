/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.heigvd.workspace;

import ch.heigvd.gemms.Constants;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;


/**
 *
 * @author mathieu
 */
public class Workspace extends StackPane implements Serializable {

   // Workspace that displays layers
   private AnchorPane workspace;
   
   // Size of workspace
   private int height;
   private int width;
   
   // Contains layers
   private LayerList layerList;
   private VBox layersController;
   
  
   /**
    * Constructor for a new instance of Workspace. The Workspace extends a Pane
    * which represents the working area of the document. It sets its initial
    * position at the center of the containing pane.
    *
    * @param width the width of the Workspace (according to the document needs)
    * @param height the height of the Workspace (according to the document
    * needs)
    */
   public Workspace(int width, int height) {
      init(width, height);
   }
   
   public void init(int width, int height) {
      this.width = width;
      this.height = height;
      
      workspace = new AnchorPane();
      this.getChildren().add(workspace);
        
      //setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
      //setClip(new Rectangle(getPrefWidth(), getPrefHeight()));
      setId("workspaceAnchorPane"); // Set id for CSS styling

      layerList = new LayerList(workspace.getChildren());
      
      // Set the workspace Pane position to be at the center of pane
      int posX = (int) ((getPrefWidth() - width) / 2);
      int posY = (int) ((getPrefHeight() - height) / 2);
      workspace.setLayoutX(posX);
      workspace.setLayoutY(posY);

      // Set preferredSize
      workspace.setPrefSize(width, height);
      workspace.setMaxSize(width, height);
      workspace.setMinSize(width, height);
      
      workspace.setId("workspacePane");

      // Register scroll event for zoom
      setOnScroll(new EventHandler<ScrollEvent>() {
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

      addEventHandler(MouseEvent.ANY, dragEventHandler);
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
      workspace.setScaleX(workspace.getScaleX() * factor);
      workspace.setScaleY(workspace.getScaleY() * factor);
   }

   /**
    * Translates the Workspace Pane container by a (x, y) translation vector.
    *
    * @param x the x coordinate of the translation
    * @param y the y coordinate of the translation
    */
   public void move(double x, double y) {
      workspace.setTranslateX(workspace.getTranslateX() + x);
      workspace.setTranslateY(workspace.getTranslateY() + y);
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
   
   public int width() {
      return width;
   }
   
   public int height() {
      return height;
   }
   
   private void writeObject(ObjectOutputStream s) throws IOException {
      // Write size of workspace
      s.writeInt(height);
      s.writeInt(width);
      
      // Number of layer
      s.writeInt(layerList.getItems().size());
      
      for(Object n : layerList.getItems()) {
         if(Serializable.class.isInstance(n)) {
            s.writeObject(n);
         }
      }
      
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      int h = s.readInt();
      int w = s.readInt();
      
      init(w, h);
      
      int nbLayers = s.readInt();
      
      for(int i = 0; i < nbLayers; ++i) {
          addLayer((Node)s.readObject());
      }
   }
   
   /**
    * Override the default snapshot to take only workspace field
    */
   @Override
   public WritableImage snapshot(SnapshotParameters params, WritableImage image) {
       return workspace.snapshot(params, image);
   }
}
