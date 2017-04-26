/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.heigvd.workspace;

import java.util.List;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

/**
 *
 * @author mathieu
 */
public class Workspace extends AnchorPane {
   
   /**
    * Constructor for a new instance of Workspace. The Workspace extends a Pane
    * which represents the working area of the document. It sets its initial 
    * position at the center of the containing pane.
    * @param width the width of the Workspace (according to the document needs)
    * @param height the height of the Workspace (according to the document needs)
    * @param pane the application pane in which the Workspace is to be added.
    */
   public Workspace(int width, int height, Pane pane) {
      // Explciit call to parent constructor
      super();
      
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
   }
   
   private Node currentLayer;
   
   public void setCurrentLayer(Node node) {
      currentLayer = node;
   }
   
   public Node getCurrentLayer() {
      return currentLayer;
   }

   /**
    *
    * @param node
    */
   public void addLayer(Node node) {
      getChildren().add(node);
      setCurrentLayer(node);
   }

   /**
    *
    * @param node
    */
   public void removeLayer(Node node) {
      getChildren().remove(node);
   }

   /**
    *
    * @return
    */
   public List<Node> getLayers() {
      return getChildren();
   }

   /**
    *
    * @param z
    */
   public void zoom(double factor) {
      setScaleX(getScaleX() * factor);
      setScaleY(getScaleY() * factor);
   }

   /**
    * Translates the Workspace Pane container by a (x, y)  translation vector.
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

}
