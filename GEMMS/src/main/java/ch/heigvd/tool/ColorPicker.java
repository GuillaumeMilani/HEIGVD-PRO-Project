package ch.heigvd.tool;

import ch.heigvd.layer.GEMMSCanvas;
import ch.heigvd.layer.GEMMSImage;
import ch.heigvd.layer.GEMMSText;
import ch.heigvd.workspace.Workspace;
import java.util.List;
import javafx.scene.Node;
import javafx.scene.paint.Paint;

/**
 *
 * @author mathieu
 */
public class ColorPicker implements Tool {
   
   // Picked color
   private Paint pickedColor;
   
   // Workspace
   private Workspace workspace;
   
   public ColorPicker(Workspace workspace) {
      this.workspace = workspace;
      this.pickedColor = null;
   }
   
   private Node getTopLayer() {
      List<Node> layers = workspace.getCurrentLayers();
      if ( layers.isEmpty() ) {
         return null;
      } else {
         return layers.get(0);
      }
   }

   @Override
   public void mousePressed(double x, double y) {
      Node layer = getTopLayer();
      if (GEMMSText.class.isInstance(layer)) {
         pickedColor = ((GEMMSText)layer).getFill();
      } else if (GEMMSCanvas.class.isInstance(layer)) {
         // TODO read the pixel at (x, y) coordinates
      }else if (GEMMSImage.class.isInstance(layer)) {
         // TODO read the pixel at (x, y) coordinates
      }
   }

   @Override
   public void mouseDragged(double x, double y) {
      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
   }

   @Override
   public void mouseReleased(double x, double y) {
      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
   }
   
}
