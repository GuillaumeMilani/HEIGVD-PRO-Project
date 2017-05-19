package ch.heigvd.tool;

import ch.heigvd.tool.settings.SizeConfigurableTool;
import ch.heigvd.workspace.Workspace;
import javafx.scene.canvas.GraphicsContext;

/**
 * The Eraser clear canvas pixels content. 
 * @author mathieu
 */
public class Eraser extends LineTool implements SizeConfigurableTool {
   
   /**
    * Constructor
    * @param workspace the Workspace to work on
    */
   public Eraser(Workspace workspace) {
      this(workspace, -1);
   }
   
   /**
    * Constructor
    * @param workspace the workspace to work on
    * @param size the size of the Eraser
    */
   public Eraser(Workspace workspace, int size) {
      super(workspace, size);
   }
   
   /**
    * Clear the areav around the given pixel position
    * @param x the x coordinate of the pixel
    * @param y the y coordinate of the pixel
    * @param gc the GraphicsContext of the canvas
    */
   @Override
   public void drawPixel(int x, int y, GraphicsContext gc) {
      gc.clearRect(x - (size/2.0), y - (size/2.0), size, size);
   }
   
   /**
    * Set the new size of the eraser
    * @param size the new size
    */
   @Override
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
