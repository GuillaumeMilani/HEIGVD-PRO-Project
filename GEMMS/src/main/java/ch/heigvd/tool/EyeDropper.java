package ch.heigvd.tool;

import ch.heigvd.layer.GEMMSCanvas;
import ch.heigvd.layer.GEMMSImage;
import ch.heigvd.layer.GEMMSText;
import ch.heigvd.workspace.Workspace;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Transform;
import javax.imageio.ImageIO;

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
         return ((GEMMSText) layer).getFill();
      } else if (GEMMSCanvas.class.isInstance(layer) || GEMMSImage.class.isInstance(layer)) {
         
         
         // Write a snapshot of the canvas or image to be able to look up pixels
         WritableImage wi = new WritableImage((int) layer.getBoundsInParent().getWidth(), (int) layer.getBoundsInParent().getHeight());
         SnapshotParameters sp = new SnapshotParameters();
         sp.setFill(Color.TRANSPARENT);
         WritableImage snapshot = layer.snapshot(sp, wi);
         
         // Map to the beginning of the image
         double pickX = x - layer.getBoundsInParent().getMinX();
         double pickY = y - layer.getBoundsInParent().getMinY();
         
         // If inside, look up pixel colors
         if (pickX >= 0 && pickX <= wi.getWidth() && pickY >= 0 && pickY <= wi.getHeight()) {
            PixelReader pr = snapshot.getPixelReader();
            return pr.getColor((int) pickX, (int) pickY);
         }
      }

      return null;
   }

   @Override
   public void mousePressed(double x, double y) {
      pickedColor = pickColor((int) x, (int) y);
   }

   @Override
   public void mouseDragged(double x, double y) {
      pickedColor = pickColor((int) x, (int) y);
   }

   @Override
   public void mouseReleased(double x, double y) {
      pickedColor = pickColor((int) x, (int) y);
      if (pickedColor != null && Color.class.isInstance(pickedColor)) {
         ColorSet.getInstance().setColor((Color) pickedColor);
      }
   }

}
