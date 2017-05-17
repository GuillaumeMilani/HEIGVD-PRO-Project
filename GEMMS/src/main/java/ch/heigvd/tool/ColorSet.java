/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.heigvd.tool;

import static java.awt.SystemColor.text;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.ColorPicker;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

/**
 *
 * @author mathieu
 */
public class ColorSet {
   
   private ColorPack primaryColor;
   private ColorPack secondaryColor;
   private ColorPack currentColor;
   private HBox colorController = null;

   private ColorSet() {
      primaryColor = new ColorPack(Color.BLACK);
      secondaryColor = new ColorPack(Color.WHITE);
      currentColor = primaryColor;
   }

   private static class Instance {
      static final ColorSet colorSet = new ColorSet();
   }
   
   public static ColorSet getInstance(){
      return Instance.colorSet;
   }
   
   public Color getColor() {
      return currentColor.getColor();
   }
   
   public void setColor(Color color) {
      currentColor.setColor(color);
   }
   
   public HBox getColorController() {
      if (colorController == null) {
         colorController = new HBox();
         colorController.getChildren().add(primaryColor.getColorPicker());
         colorController.getChildren().add(secondaryColor.getColorPicker());
      }
      return colorController;
   }
   
   private class ColorPack {
      private Color color;
      private ColorPicker cp;
      
      public ColorPack(Color color) {
         this.color = color;
         cp = new ColorPicker();
         cp.setValue(color);
         cp.setPrefWidth(40);
         cp.setOnAction(new EventHandler() {
            public void handle(Event t) {
               ColorPack.this.color = cp.getValue();
            }
        });
      }
      
      public Color getColor() {
         return color;
      }
      
      public void setColor(Color color) {
         this.color = color;
      }
      
      public ColorPicker getColorPicker() {
         return cp;
      }
      
      
   }
}
