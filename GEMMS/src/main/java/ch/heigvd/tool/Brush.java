package ch.heigvd.tool;

import ch.heigvd.workspace.LayerListable;
import ch.heigvd.workspace.Workspace;
import static java.lang.System.gc;
import java.util.List;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * The Brush tool class. Brush objects are tools that draw lines on a JavafX
 * Canvas object. It is possible to set the color and the width of the brush.
 * @author mathieu
 */
public class Brush implements Tool {
   // The workspace on which this brush is currently working
   Workspace workspace;
   
   // Color of the brush
   Color color;
   
   // Size of the brush in pixels
   int size;
   
   // Keep track of past x, y coordinates
   private double x;
   private double y;
   
   /**
    * Constructor. Sets the default usage values which are a the color black
    * and a size of 1px.
    * @param workspace the Workspace to work on
    */
   public Brush(Workspace workspace) {
      this.workspace = workspace;
      this.color = Color.BLACK;
      this.size = 1;
   }
   
   /**
    * Constructor. Specifies the color and size.
    * @param workspace the Workspace to work on
    * @param color the color of the brush
    * @param size the size [px]
    */
   public Brush(Workspace workspace, Color color, int size) {
      this(workspace);
      this.color = color;
      this.size = size;
   }
   
   /**
    * Method to call on the start of the dragging movement, when the mouse is 
    * pressed for the first time.
    * @param x the x coordinate of the event.
    * @param y the y coordinate of the event.
    */
   @Override
   public void mousePressed(double x, double y) {
      this.x = x;
      this.y = y;
   }
   
   /**
    * Method to call during the drag movement of the mouse event (after calling
    * mosuePressed once).
    * @param x the x coordinate of the event
    * @param y the y coordinate of the event
    */
   @Override
   public void mouseDragged(double x, double y) {
      
      // Get the selected layers of the workspace
      List<Node> layers = workspace.getCurrentLayers();
      
      // For each node, draw on it
      for (Node node : layers) {
         if (Canvas.class.isInstance(node)) {
            Canvas canvas = (Canvas) node;
            GraphicsContext gc = canvas.getGraphicsContext2D();

            gc.setStroke(color);
            gc.setLineWidth(size);

            gc.strokeLine(this.x, this.y, x, y);
         }

      }
      
      // Update the position
      this.x = x;
      this.y = y;
   }
   
   /**
    * Method to call at the end of the drag movement. 
    * @param x the x coordinate of the event
    * @param y the y coordinate of the event
    */
   @Override
   public void mouseReleased(double x, double y) {

   }
   
   /**
    * Set the color of the brush.
    * @param color the new Color
    */
   public void setColor(Color color) {
      this.color = color;
   }
   
   /**
    * Get the current brush color.
    * @return the color of the brush
    */
   public Color getColor() {
      return color;
   }
   
   /**
    * Set the new size of the brush
    * @param size the new size
    */
   public void setSize(int size) {
      this.size = size;
   }
   
   /**
    * Get the size of the brush in pixels
    * @return the size of the brush
    */
   public int getSize() {
      return size;
   }

}
