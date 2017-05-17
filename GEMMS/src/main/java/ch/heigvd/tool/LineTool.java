package ch.heigvd.tool;

import ch.heigvd.workspace.Workspace;
import java.util.List;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

/**
 * The LineTool class represents objects that need to take actions following the
 * mouse such as a paint brush, or an eraser. The LineTool is in charge of
 * "drawing" perfect lines (using the Bresenham algorithm), calling the
 * drawPixel method each time it should paint or more generally apply a
 * treatment to a pixel of the canvas.
 *
 * @author mathieu
 */
public abstract class LineTool implements Tool {

   // The Workspace the object is working on
   protected Workspace workspace;

   // The size of the tool [px]
   protected int size;

   // Last registered coordinates of the tool
   protected int x;
   protected int y;

   protected boolean started = false;

   /**
    * Constructor.
    *
    * @param workspace the Workspace to work on
    * @param size the size of the tool in pixels
    */
   public LineTool(Workspace workspace, int size) {
      this.workspace = workspace;
      this.size = size;
   }

   /**
    * The method line implements the Bresenham algorithm in order to draw smooth
    * lines on every mouse drag movement.
    *
    * This compact version of the algorithm comes from this website:
    *
    * https://de.wikipedia.org/wiki/Bresenham-Algorithmus
    *
    * on 16.05.2017
    *
    * The method calls the abstract method drawPixel for each pixel it needs to
    * apply a action on.
    *
    * @param x0 the x coordinate of the first point
    * @param y0 the y coordinate of the first point
    * @param x1 the x coordinate of the second point
    * @param y1 the y coordinate of the second point
    * @param gc the GraphicsContext from the canvas to apply the tool on
    */
   public void line(int x0, int y0, int x1, int y1, GraphicsContext gc) {
      int dx = Math.abs(x1 - x0), sx = x0 < x1 ? 1 : -1;
      int dy = -Math.abs(y1 - y0), sy = y0 < y1 ? 1 : -1;
      int err = dx + dy, e2;

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
         if (e2 < dx) {
            err += dx;
            y0 += sy;
         }
      }
   }

   /**
    * Method to call on the start of the dragging motion, when the mouse is
    * pressed for the first time.
    *
    * @param x the x coordinate of the event.
    * @param y the y coordinate of the event.
    */
   @Override
   public void mousePressed(double x, double y) {
      this.x = (int) x;
      this.y = (int) y;

      started = true;
   }

   /**
    * Method to call during the dragging motion.
    *
    * @param x the event x coordinate
    * @param y the event y coordinate
    */
   @Override
   public void mouseDragged(double x, double y) {

      if (started) {

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
      } else {
         started = true;
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

   /**
    * Method to define the action to apply to each painted pixel by the
    * Bresenham algorithm
    *
    * @param x the x coordinate of the pixel
    * @param y the y coordinate of the pixel
    * @param gc the GraphicsContext of the canvas
    */
   protected abstract void drawPixel(int x, int y, GraphicsContext gc);
}
