package ch.heigvd.tool;

import ch.heigvd.workspace.Workspace;
import javafx.scene.canvas.GraphicsContext;

/**
 *
 * @author mathieu
 */
public class Eraser extends LineTool {

   public Eraser(Workspace workspace) {
      this(workspace, 10);
   }
   
   public Eraser(Workspace workspace, int size) {
      super(workspace, size);
   }
   
   @Override
   public void drawPixel(int x, int y, GraphicsContext gc) {
      gc.clearRect(x - (size/2.0), y - (size/2.0), size, size);
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
