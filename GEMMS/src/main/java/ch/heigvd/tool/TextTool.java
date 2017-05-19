/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.heigvd.tool;

import ch.heigvd.layer.GEMMSText;
import ch.heigvd.workspace.Workspace;
import java.util.List;
import java.util.Optional;
import javafx.scene.Node;
import javafx.scene.control.TextInputDialog;
import javafx.scene.text.Font;

/**
 *
 * @author mathieu
 */
public class TextTool implements Tool, SizeConfigurable {

   private final Workspace workspace;

   public TextTool(Workspace workspace) {
      this.workspace = workspace;
   }
   
   public static Optional<String> getPromptValue() {
      TextInputDialog dialog = new TextInputDialog();
      dialog.setContentText("Please enter the text value:");
      return dialog.showAndWait();
   }

   public static void promptTextValue(List<Node> layers) {
      Optional<String> result = getPromptValue();
      if (result.isPresent()) {
         for (Node node : layers) {
            if (node instanceof GEMMSText) {
               ((GEMMSText) node).setText(result.get());
            }
         }
      }
   }

   @Override
   public void mousePressed(double x, double y) {
   }

   @Override
   public void mouseDragged(double x, double y) {
   }

   @Override
   public void mouseReleased(double x, double y) {
      List<Node> layers = workspace.getCurrentLayers();
      boolean atLeastOneText = false;
      for (Node layer : layers) {
         if (layer instanceof GEMMSText) {
            atLeastOneText = true;
         }
      }
      
      if (atLeastOneText) {
         TextTool.promptTextValue(layers);
      }

   }

   @Override
   public void setSize(int size) {
      for (Node n : workspace.getCurrentLayers()) {
         if (n instanceof GEMMSText) {
            GEMMSText t = (GEMMSText) n;
            t.setFontSize(size);
         }
      }
   }

}
