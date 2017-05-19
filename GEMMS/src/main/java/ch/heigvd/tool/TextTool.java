package ch.heigvd.tool;

import ch.heigvd.tool.settings.ColorConfigurableTool;
import ch.heigvd.tool.settings.SizeConfigurableTool;
import ch.heigvd.layer.GEMMSText;
import ch.heigvd.workspace.Workspace;
import java.util.List;
import java.util.Optional;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;

/**
 * The text tool manages GEMMSTexts, changing their sizes when a user clicks 
 * on it.
 * @author mathieu
 */
public class TextTool implements Tool, SizeConfigurableTool, ColorConfigurableTool {
   // The Workspace to work on
   private final Workspace workspace;
   
   /**
    * Constructor. Sets the workspace to work on.
    * @param workspace the workspace to work on
    */
   public TextTool(Workspace workspace) {
      this.workspace = workspace;
   }

   /**
    * Get a dialog window to ask for a user text input. The result 
    * is either null if the user didn't enter anything, either a String 
    * containing paragraphs.
    * 
    * 
    * @param def the default value of the input text area field
    * @return a String either null or containing user input
    */
   public static Optional<String> getDialogText(String def) {
      // Create Dialog
      Dialog dialog = new Dialog<>();
      
      // Set the title
      dialog.setTitle("Please enter your text.");

      // Set button
      dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

      // Set text field
      GridPane grid = new GridPane();
      grid.setHgap(10);
      grid.setVgap(10);
      grid.setPadding(new Insets(20, 150, 10, 10));
      
      // Set prompt invite
      Label prompt = new Label("Please enter your text");
      
      // Set the text area
      final TextArea text = new TextArea();
      if (def != null)
         text.setText(def);
      
      // Add elements
      grid.add(prompt, 0, 0);
      grid.add(text, 0, 1);
      
      // Add to the dialog
      dialog.getDialogPane().setContent(grid);

      // Request focus
      Platform.runLater(() -> text.requestFocus());

      // Return result
      dialog.setResultConverter(dialogButton -> {
         if (dialogButton == ButtonType.OK) {
            List<CharSequence> ps = text.getParagraphs();
            String result = "";
            int i = 0;
            for (CharSequence p : ps) {
               result += p.toString();
               if (++i < ps.size())
                  result += "\n";
            }
            return result;
         }

         return null;
      });
      
      return dialog.showAndWait();
   }
   
   /**
    * Request a dialog text input, and apply the changes to all GEMMSText
    * layers contained in the parameter layers
    * @param layers a list of layers
    */
   private static void dialogTextValue(List<Node> layers) {
      
      // Check if there is only one text selected
      if (layers.size() == 1) {
         if (layers.get(0) instanceof GEMMSText) {
            String def = ((GEMMSText)layers.get(0)).getText();
            Optional<String> result = getDialogText(def);
            // Modify all GEMMSText layers
            if (result.isPresent()) {
               for (Node node : layers) {
                  if (node instanceof GEMMSText) {
                     GEMMSText text = (GEMMSText)node;
                     text.setText(result.get());
                     text.setFontSize((int)text.getFont().getSize());
                  }
               }
            }
         }
      }
   }
   
   /**
    * Do nothing on mouse pressed
    * @param x
    * @param y 
    */
   @Override
   public void mousePressed(double x, double y) {
   }
   
   /**
    * Do nothing on mouse drag
    * @param x
    * @param y 
    */
   @Override
   public void mouseDragged(double x, double y) {
   }
   
   /**
    * Set texts on mouse released
    * @param x
    * @param y 
    */
   @Override
   public void mouseReleased(double x, double y) {
      List<Node> layers = workspace.getCurrentLayers();
      if (layers.size() == 1 && layers.get(0) instanceof GEMMSText) {
         GEMMSText layer = (GEMMSText)layers.get(0);
         int layerW = (int)layer.getBoundsInParent().getWidth();
         int layerH = (int)layer.getBoundsInParent().getHeight();
         int layerX = (int)(layer.getX() + layer.getTranslateX());
         int layerY = (int)(layer.getY() + layer.getTranslateY() - layerH/2);
         if (x >= layerX && y >= layerY && x <= layerX + layerW && y <= layerY + layerH) {
            TextTool.dialogTextValue(layers);
         }
      }

   }
   
   /**
    * Set size of the current layers
    * @param size 
    */
   @Override
   public void setSize(int size) {
      List<Node> layers = workspace.getCurrentLayers();
      if (layers.size() == 1 && layers.get(0) instanceof GEMMSText) {
            GEMMSText t = (GEMMSText) layers.get(0);
            t.setFontSize(size);
      }
   }

   @Override
   public void setColor(Color color) {
      List<Node> layers = workspace.getCurrentLayers();
      if (layers.size() == 1 && layers.get(0) instanceof GEMMSText) {
            GEMMSText t = (GEMMSText) layers.get(0);
            t.setFill(color);
      }
   }

   @Override
   public int getSize() {
      List<Node> layers = workspace.getCurrentLayers();
      if (layers.size() == 1 && layers.get(0) instanceof GEMMSText) {
            GEMMSText t = (GEMMSText) layers.get(0);
            return (int)t.getFont().getSize();
      } else {
         return -1;
      }
   }

   @Override
   public Color getColor() {
      List<Node> layers = workspace.getCurrentLayers();
      if (layers.size() == 1 && layers.get(0) instanceof GEMMSText) {
            GEMMSText t = (GEMMSText) layers.get(0);
            return (Color)t.getFill();
      } else {
         return null;
      }
   }

}
