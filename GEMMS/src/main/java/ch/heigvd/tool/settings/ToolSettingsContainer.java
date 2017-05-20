package ch.heigvd.tool.settings;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Popup;

/**
 * ToolSettingsContainer are meant to contain ToolSettings.
 * @author mathieu
 */
public class ToolSettingsContainer extends Popup {
   public ToolSettingsContainer(Node... settings) {
      // Set HBox container
      final HBox hbox = new HBox();
      hbox.setAlignment(Pos.CENTER);
      hbox.setBackground(new Background(new BackgroundFill(Color.DARKGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
      
      // Set padding and alignement
      hbox.setPadding(new Insets(5, 5, 5, 5));
      hbox.setSpacing(10);
      hbox.setAlignment(Pos.CENTER);
      
      // Add elements
      for(Node s : settings) {
         hbox.getChildren().add(s);
      }
      
      getContent().add(hbox);
   }
}
