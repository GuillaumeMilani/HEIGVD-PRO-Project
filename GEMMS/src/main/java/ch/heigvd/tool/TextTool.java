package ch.heigvd.tool;

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
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;

/**
 * The text tool manages GEMMSTexts, changing their sizes when a user clicks 
 * on it.
 * @author mathieu
 */
public class TextTool implements Tool, SizeConfigurable, ColorConfigurable {
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
            for (CharSequence p : ps) {
               result += p.toString() + System.getProperty("line.separator");;
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
      int numberTexts = 0;
      String def = null;
      for (Node layer : layers) {
         if (layer instanceof GEMMSText) {
            numberTexts++;
            def = ((GEMMSText)layer).getText();
         }
      }
      
      // Get the dialog result
      Optional<String> result;
      if (numberTexts == 1 ) {
         result = getDialogText(def);
      } else {
         result = getDialogText(null);
      }
      
      // Modify all GEMMSText layers
      if (result.isPresent()) {
         for (Node node : layers) {
            if (node instanceof GEMMSText) {
               ((GEMMSText) node).setText(result.get());
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
      boolean atLeastOneText = false;
      for (Node layer : layers) {
         if (layer instanceof GEMMSText) {
            atLeastOneText = true;
         }
      }

      if (atLeastOneText) {
         TextTool.dialogTextValue(layers);
      }

   }
   
   /**
    * Set size of the current layers
    * @param size 
    */
   @Override
   public void setSize(int size) {
      for (Node n : workspace.getCurrentLayers()) {
         if (n instanceof GEMMSText) {
            GEMMSText t = (GEMMSText) n;
            t.setFontSize(size);
         }
      }
   }

   @Override
   public void setColor(Color color) {
      for (Node n : workspace.getCurrentLayers()) {
         if (n instanceof GEMMSText) {
            GEMMSText t = (GEMMSText) n;
            t.setFill(color);
         }
      }
   }

}
