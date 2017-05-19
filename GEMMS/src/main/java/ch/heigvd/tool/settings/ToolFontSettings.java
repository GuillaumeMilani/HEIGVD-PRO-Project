/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.heigvd.tool.settings;

import ch.heigvd.layer.GEMMSText;
import ch.heigvd.workspace.Workspace;
import java.util.List;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.text.Font;

/**
 *
 * @author mathieu
 */
public class ToolFontSettings extends ToolSettings{

   // Tool target
   private FontConfigurableTool target = null;

   //
   private ComboBox<String> cb;

   public ToolFontSettings() {
      cb = new ComboBox<>();

      List<String> fontFamilies = javafx.scene.text.Font.getFamilies();
      for (String font : javafx.scene.text.Font.getFamilies()) {
         cb.getItems().add(font);
      }
      cb.setValue(fontFamilies.get(0));
      cb.valueProperty().addListener(new ChangeListener<String>() {
         @Override
         public void changed(ObservableValue value, String old_value, String new_value) {
            if (target != null) {
               target.setFont(new_value);
            }
         }
      });
      
      getChildren().add(cb);
   }
   
   public void setTarget(FontConfigurableTool target) {
      this.target = target;
      String fontName = target.getFont();
      if (fontName == null) {
         target.setFont(cb.getValue());
      } else {
         cb.setValue(target.getFont());
      }
   }
   
   public Font getFont() {
      return Font.font(cb.getValue());
   }
}
