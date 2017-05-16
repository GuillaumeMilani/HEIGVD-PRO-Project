package ch.heigvd.tool;

import ch.heigvd.workspace.Workspace;
import java.util.List;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 *
 * @author mathieu
 */
public class Eraser implements Tool {

   // The workspace on which this eraser is currently working
   Workspace workspace;
   
   // Size of the eraser
   int size;

   // Keep track of past x, y coordinates
   private double x;
   private double y;

   public Eraser(Workspace workspace) {
      this.workspace = workspace;
      this.size = 10;
   }
   
   public Eraser(Workspace workspace, int size) {
      this(workspace);
      this.size = size;
   }

   @Override
   public void mousePressed(double x, double y) {
      this.x = x;
      this.y = y;
      
      
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
            
            gc.clearRect(this.x - (size/2.0), this.y - (size/2.0), size, size);

            //gc.setStroke(Color.WHITE);
            //gc.setLineWidth(size);

            //gc.strokeLine(this.x, this.y, x, y);
         }

      }
      
      // Update the position
      this.x = x;
      this.y = y;
   }
   
   @Override
   public void mouseReleased(double x, double y) {

   }
   
   /**
    * Set the new size of the eraser
    * @param size the new size
    */
   public void setSize(int size) {
      this.size = size;
   }
   
   /**
    * Get the size of the eraser in pixels
    * @return the size of the brush
    */
   public int getSize() {
      return size;
   }

}
