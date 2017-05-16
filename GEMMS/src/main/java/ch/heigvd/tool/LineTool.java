/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.heigvd.tool;

import ch.heigvd.workspace.Workspace;
import java.util.List;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

/**
 *
 * @author mathieu
 */
public abstract class LineTool implements Tool {

   protected Workspace workspace;

   protected int size;

   protected int x;
   protected int y;

   public LineTool(Workspace workspace, int size) {
      this.workspace = workspace;
      this.size = size;
   }

   protected abstract void drawPixel(int x, int y, GraphicsContext gc);

   public void line(int x0, int y0, int x1, int y1, GraphicsContext gc) {
      int dx = Math.abs(x1 - x0), sx = x0 < x1 ? 1 : -1;
      int dy = -Math.abs(y1 - y0), sy = y0 < y1 ? 1 : -1;
      int err = dx + dy, e2;
      /* error value e_xy */

      while (true) {
         drawPixel(x0, y0, gc);
         if (x0 == x1 && y0 == y1) {
            break;
         }
         e2 = 2 * err;
         if (e2 > dy) {
            err += dy;
            x0 += sx;
         }
         /* e_xy+e_x > 0 */
         if (e2 < dx) {
            err += dx;
            y0 += sy;
         }
         /* e_xy+e_y < 0 */
      }
   }

   /**
    * Method to call on the start of the dragging movement, when the mouse is
    * pressed for the first time.
    *
    * @param x the x coordinate of the event.
    * @param y the y coordinate of the event.
    */
   @Override
   public void mousePressed(double x, double y) {
      this.x = (int) x;
      this.y = (int) y;
   }

   @Override
   public void mouseDragged(double x, double y) {

      // Get the selected layers of the workspace
      List<Node> layers = workspace.getCurrentLayers();

      // For each node, draw on it
      for (Node node : layers) {
         if (Canvas.class.isInstance(node)) {
            Canvas canvas = (Canvas) node;
            GraphicsContext gc = canvas.getGraphicsContext2D();

            line((int) this.x, (int) this.y, (int) x, (int) y, gc);

         }

      }

      // Update the position
      this.x = (int) x;
      this.y = (int) y;
   }

   /**
    * Method to call at the end of the drag movement.
    *
    * @param x the x coordinate of the event
    * @param y the y coordinate of the event
    */
   @Override
   public void mouseReleased(double x, double y) {

   }
}
