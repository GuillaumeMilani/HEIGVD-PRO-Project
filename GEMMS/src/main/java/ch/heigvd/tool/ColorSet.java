/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.heigvd.tool;

import javafx.scene.paint.Color;

/**
 *
 * @author mathieu
 */
public class ColorSet {
   
   private ColorPack primaryColor;
   private ColorPack secondaryColor;
   private ColorPack currentColor;

   private ColorSet() {
      primaryColor = new ColorPack(Color.BLACK);
      secondaryColor = new ColorPack(Color.WHITE);
      currentColor = primaryColor;
   }

   private static class Instance {
      static final ColorSet colorSet = new ColorSet();
   }
   
   public ColorSet getInstance(){
      return Instance.colorSet;
   }
   
   public Color getColor() {
      return currentColor.getColor();
   }
   
   public void setColor(Color color) {
      currentColor.setColor(color);
   }
   
   private class ColorPack {
      private Color color;
      
      public ColorPack(Color color) {
         this.color = color;
      }
      
      public Color getColor() {
         return color;
      }
      
      public void setColor(Color color) {
         this.color = color;
      }
      
      
   }
}
