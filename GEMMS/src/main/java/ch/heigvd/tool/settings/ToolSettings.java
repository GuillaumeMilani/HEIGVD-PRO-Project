package ch.heigvd.tool.settings;

import javafx.geometry.Insets;
import javafx.scene.layout.HBox;

/**
 * ToolSettings is a general abstraction for a component which purpose is to 
 * manage a specific tool instance settings.
 * 
 * It extends HBox in order to be used as a block in an other containing Node.
 * @author mathieu
 */
public abstract class ToolSettings extends HBox{
   
   /**
    * Set a general spaceing of 10 pixels.
    */
   public ToolSettings() {
      setSpacing(10);
   }
}
