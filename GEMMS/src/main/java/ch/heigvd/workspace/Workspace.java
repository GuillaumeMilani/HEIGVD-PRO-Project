/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.heigvd.workspace;

import ch.heigvd.gemms.Constants;
import ch.heigvd.tool.Brush;
import ch.heigvd.tool.Tool;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Transform;

/**
 * @author mathieu
 */
public class Workspace extends StackPane implements Serializable {

   // Workspace that displays layers
   private AnchorPane workspace;
   
   // Layer for object's tool
   private AnchorPane layerTools;

   // Size of workspace
   private int height;
   private int width;

   // Contains layers
   private LayerList layerList;
   private VBox layersController;

   // current selected tool
   private Tool currentTool;

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
      
      layerTools = new AnchorPane();
      this.getChildren().add(layerTools);

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
      
      
      // Stack the layer tool on workspace
      layerTools.setLayoutX(posX);
      layerTools.setLayoutY(posY);
      layerTools.setPrefSize(width, height);
      layerTools.setMaxSize(width, height);
      layerTools.setMinSize(width, height);
      

      currentTool = null;

      // Add a mouse event to manage the current tool actions
      layerTools.addEventHandler(MouseEvent.ANY, new EventHandler<MouseEvent>() {

         @Override
         public void handle(MouseEvent event) {
            if (currentTool != null) {
                
                // Get mouse position
                Point3D p = new Point3D(event.getX(), event.getY(), 0);
                
                if(getCurrentLayers().size() > 0) {
                    for(Transform t : getCurrentLayers().get(0).getTransforms()) {
                        p = t.transform(p.getX(), p.getY(), p.getZ());
                    }
                }
                
               if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
                  currentTool.mousePressed(p.getX(), p.getY());
               } else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                  currentTool.mouseDragged(p.getX(), p.getY());
               } else if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
                  currentTool.mouseReleased(p.getX(), p.getY());
               }
            }
         }
      });

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
            } else {
               x = event.getX();
               y = event.getY();
            }
         }
      };

      addEventFilter(MouseEvent.ANY, dragEventHandler);
   }

   public List<Node> getCurrentLayers() {
      return layerList.getSelectionModel().getSelectedItems();
   }

   /**
    * @param node
    */
   public void addLayer(Node node) {
      layerList.getItems().add(node);
      layerList.getSelectionModel().clearSelection();
      layerList.getSelectionModel().selectLast();
   }

   /**
    * @param node
    */
   public void removeLayer(Node node) {
      layerList.getItems().remove(node);
   }

   /**
    * @return
    */
   public List<Node> getLayers() {
      return layerList.getItems();
   }

   /**
    * @param factor
    */
   public void zoom(double factor) {
      workspace.setScaleX(workspace.getScaleX() * factor);
      workspace.setScaleY(workspace.getScaleY() * factor);
      layerTools.setScaleX(layerTools.getScaleX() * factor);
      layerTools.setScaleY(layerTools.getScaleY() * factor);
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
      layerTools.setTranslateX(layerTools.getTranslateX() + x);
      layerTools.setTranslateY(layerTools.getTranslateY() + y);
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
      }
      return layersController;
   }

   public void setCurrentTool(Tool tool) {
      layerTools.getChildren().clear();
      this.currentTool = tool;
   }

   public Tool getCurrentTool() {
      return currentTool;
   }

   public int width() {
      return width;
   }

   public int height() {
      return height;
   }
   
   public double getWorkspaceScaleX() {
      return workspace.getScaleX();
   }
   
   public double getWorkspaceScaleY() {
      return workspace.getScaleY();
   }
   
   public AnchorPane getLayerTool() {
       return layerTools;
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      // Write size of workspace
      s.writeInt(height);
      s.writeInt(width);

      // Number of layer
      s.writeInt(layerList.getItems().size());

      for (Object n : layerList.getItems()) {
         if (Serializable.class.isInstance(n)) {
            s.writeObject(n);
         }
      }

   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      int h = s.readInt();
      int w = s.readInt();

      init(w, h);

      int nbLayers = s.readInt();

      for (int i = 0; i < nbLayers; ++i) {
         addLayer((Node) s.readObject());
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
