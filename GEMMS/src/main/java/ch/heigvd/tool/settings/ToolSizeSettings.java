package ch.heigvd.tool.settings;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Slider;
import javafx.scene.text.Text;

/**
 *
 * @author mathieu
 */
public class ToolSizeSettings extends ToolSettings {
   // The target to configure
   private SizeConfigurableTool target;
   // The slider to set the size
   private final Slider slider;

   public ToolSizeSettings(int min, int max, int value) {
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
               target.setSize(new_val.intValue());
            }
         }
      });
      
      getChildren().add(slider);
      getChildren().add(textValue);
   }
   
   public void setTarget(SizeConfigurableTool target) {
      this.target = target;
      int size = target.getSize();
      if (size > 0) {
         slider.setValue(size);
      } else {
         target.setSize((int)slider.getValue());
      }
   }
   
   public int getSize() {
      return (int)slider.getValue();
   }
}
