package ch.heigvd.tool;

import ch.heigvd.layer.GEMMSCanvas;
import ch.heigvd.layer.GEMMSImage;
import ch.heigvd.layer.GEMMSText;
import ch.heigvd.workspace.Workspace;
import java.util.List;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/**
 *
 * @author mathieu
 */
public class EyeDropper implements Tool {

   // Picked color
   private Paint pickedColor;

   // Workspace
   private Workspace workspace;

   public EyeDropper(Workspace workspace) {
      this.workspace = workspace;
      this.pickedColor = null;
   }

   private Node getTopLayer() {
      List<Node> layers = workspace.getCurrentLayers();
      if (layers.isEmpty()) {
         return null;
      } else {
         return layers.get(0);
      }
   }

   private Paint pickColor(int x, int y) {
      Node layer = getTopLayer();
      if (GEMMSText.class.isInstance(layer)) {
         return((GEMMSText) layer).getFill();
      } else if (GEMMSCanvas.class.isInstance(layer) || GEMMSImage.class.isInstance(layer)) {

         WritableImage wi = new WritableImage((int)layer.getBoundsInParent().getWidth(), (int)layer.getBoundsInParent().getHeight());
         WritableImage snapshot = layer.snapshot(new SnapshotParameters(), wi);
         PixelReader pr = snapshot.getPixelReader();

         return pr.getColor((int) x, (int) y);

      }
      
      return null;
   }

   @Override
   public void mousePressed(double x, double y) {
      pickedColor = pickColor((int)x, (int)y);
   }

   @Override
   public void mouseDragged(double x, double y) {
      pickedColor = pickColor((int)x, (int)y);
   }

   @Override
   public void mouseReleased(double x, double y) {
      pickedColor = pickColor((int)x, (int)y);
      if (pickedColor != null && Color.class.isInstance(pickedColor)) {
         ColorSet.getInstance().setColor((Color)pickedColor);
      }
   }

}
