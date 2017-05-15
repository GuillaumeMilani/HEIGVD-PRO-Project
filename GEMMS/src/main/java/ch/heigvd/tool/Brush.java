/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.heigvd.tool;

import ch.heigvd.workspace.Workspace;
import static java.lang.System.gc;
import java.util.List;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 *
 * @author mathieu
 */
public class Brush implements Tool {

   Workspace workspace;

   private double x;
   private double y;

   public Brush(Workspace workspace) {
      this.workspace = workspace;
   }

   @Override
   public void mousePressed(double x, double y) {
      this.x = x;
      this.y = y;
   }

   @Override
   public void mouseDragged(double x, double y) {

      List<Node> layers = workspace.getCurrentLayers();

      for (Node node : layers) {
         Canvas canvas = (Canvas) node;
         GraphicsContext gc = canvas.getGraphicsContext2D();

         gc.setStroke(Color.BLUE);
         gc.setLineWidth(5);

         gc.strokeLine(this.x, this.y, x, y);

      }
      
      System.out.println("lol");
      
      this.x = x;
      this.y = y;

   }

   @Override
   public void mouseReleased(double x, double y) {

   }

}
