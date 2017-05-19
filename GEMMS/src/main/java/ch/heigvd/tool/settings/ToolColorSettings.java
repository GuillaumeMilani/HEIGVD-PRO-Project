/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.heigvd.tool.settings;

import ch.heigvd.tool.ColorSet;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;

/**
 *
 * @author mathieu
 */
public class ToolColorSettings extends ToolSettings {

   // The target to configure
   private ColorConfigurableTool target = null;

   // Color picker
   private ColorPicker cp;

   public ToolColorSettings(Color defaultColor) {
      cp = new ColorPicker();
      cp.setValue(defaultColor);
      cp.getStyleClass().add("button");
      cp.setOnAction(new EventHandler() {
         @Override
         public void handle(Event t) {
            target.setColor(cp.getValue());
         }
      });
      cp.getStyleClass().add("small");
      
      getChildren().add(cp);
   }
   public void setTarget(ColorConfigurableTool target) {
      this.target = target;
      Color color = target.getColor();
      if (color != null) {
         cp.setValue(color);
      } else {
         cp.setValue(ColorSet.getInstance().getColor());
         target.setColor(cp.getValue());
      }
   }
}
