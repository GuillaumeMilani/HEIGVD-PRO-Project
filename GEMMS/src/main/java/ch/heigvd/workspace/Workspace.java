/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.heigvd.workspace;

import java.util.List;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;

/**
 *
 * @author mathieu
 */
public class Workspace extends Group {
   
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
   public void zoom(int z) {

   }

   /**
    *
    * @param x
    * @param y
    */
   public void move(int x, int y) {

   }

   /**
    *
    */
   public void crop() {

   }

}
