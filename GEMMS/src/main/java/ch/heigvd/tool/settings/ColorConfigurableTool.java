package ch.heigvd.tool.settings;

import javafx.scene.paint.Color;

/**
 * this interface represents tools that can change color. The ToolColorSettings
 * objects use this interface to manage a tool color.
 * @author mathieu
 */
public interface ColorConfigurableTool {
   /**
    * Set the tool current color
    * @param color 
    */
   public void setColor(Color color);
   
   /**
    * This method is used by ToolColorSettings to determine whether they 
    * should change the tool current color. If the method returns null, they
    * will set their current color. 
    * 
    * If the method return a valid color, they won't update their color.
    * @return 
    */
   public Color getColor();
}
