/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.heigvd.tool.settings;

import java.util.List;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 *
 * @author mathieu
 */
public class ToolFontSettings extends ToolSettings{

   // Tool target
   private FontConfigurableTool target = null;

   // List of fonts
   private ComboBox<String> cb;
   
   private Slider slider;

   public ToolFontSettings(int min, int max, int value) {
      
      setAlignment(Pos.CENTER_LEFT);
      
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
               target.setFont(Font.font(cb.getValue(), slider.getValue()));
            }
         }
      });
      
      
      // Create the slider
      slider = new Slider(min, max, value);
      
      // Value displayer
      final Text textValue = new Text(String.valueOf(value));
      
      // Create the event on slider change
      slider.valueProperty().addListener(new ChangeListener<Number>() {
         @Override
         public void changed(ObservableValue<? extends Number> ov,
                 Number old_val, Number new_val) {
            if (target != null) {
               textValue.setText(String.format("%d", new_val.intValue()));
               target.setFont(Font.font(cb.getValue(), slider.getValue()));
            }
         }
      });
      
      
      getChildren().add(slider);
      getChildren().add(textValue);
      getChildren().add(cb);
   }
   
   public void setTarget(FontConfigurableTool target) {
      this.target = target;
      Font font = target.getFont();
      if (font == null) {
         target.setFont(Font.font(cb.getValue(), slider.getValue()));
      } else {
         cb.setValue(font.getFamily());
         slider.setValue(font.getSize());
      }
   }
   
   public Font getFont() {
      return Font.font(cb.getValue(), (int)slider.getValue());
   }
}
