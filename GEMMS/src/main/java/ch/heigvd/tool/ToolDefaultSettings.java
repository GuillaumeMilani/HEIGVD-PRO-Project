/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.heigvd.tool;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Popup;

/**
 *
 * @author mathieu
 */
public class ToolDefaultSettings extends Popup {

   private Button button;
   private DefaultParametrable target;

   public ToolDefaultSettings(Button button) {

      this.button = button;

      // Set width of the popup
      setWidth(200);

      final HBox hbox = new HBox();
      hbox.setAlignment(Pos.CENTER);
      hbox.setPrefHeight(40);
      hbox.setBackground(new Background(new BackgroundFill(Color.DARKGRAY, CornerRadii.EMPTY, Insets.EMPTY)));

      final ColorPicker cp = new ColorPicker();
      cp.setStyle("-fx-color-label-visible: false ;");
      cp.setOnAction(new EventHandler() {
         public void handle(Event t) {
            if (target != null) {
               target.setColor(cp.getValue());
            }
         }
      });
      final Slider sl = new Slider(0, 100, 50);

      hbox.getChildren().add(cp);
      hbox.getChildren().add(sl);

      // Add the content of the popup
      getContent().add(hbox);
   }

   public void setTargetTool(DefaultParametrable target) {
      this.target = target;
   }
}
