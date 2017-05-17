package ch.heigvd.tool;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Point3D;
import javafx.scene.control.ColorPicker;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.transform.Transform;

/**
 * ColorSet is a class that represents the application color set. It is composed
 * of two main ColorPack, which keep track of two colors that the application
 * interface is currently using.
 *
 * The class uses the singleton pattern to provide the unique instance of
 * ColorSet.
 *
 * @author mathieu
 */
public class ColorSet {

   // Primary ColorPack
   private final ColorPack primaryColor;
   // Secondary ColorPack
   private final ColorPack secondaryColor;
   // The current selected ColorPack
   private ColorPack currentColor;
   // The ColorSet controller to display the controls of the instance of ColorSet
   private HBox colorController = null;

   /**
    * Constructor. Defines the default ColorPacks.
    */
   private ColorSet() {
      primaryColor = new ColorPack(Color.BLACK);
      secondaryColor = new ColorPack(Color.WHITE);
      currentColor = primaryColor;
   }

   /**
    * Java trick to create the singleton instance
    */
   private static class Instance {

      static final ColorSet colorSet = new ColorSet();
   }

   /**
    * Get the unique instance of the ColorSet
    *
    * @return the unique ColorSet instance
    */
   public static ColorSet getInstance() {
      return Instance.colorSet;
   }

   /**
    * Get the current color of the ColorSet. It gives the current ColorPack
    * color.
    *
    * @return the current color
    */
   public Color getColor() {
      return currentColor.getColor();
   }

   /**
    * Set the current ColorPack's color
    *
    * @param color the new color
    */
   public void setColor(Color color) {
      currentColor.setColor(color);
   }

   /**
    * Get the controller for the ColorSet. The controller is a HBox that
    * contains the element controlling the ColorPacks and the ColorSet.
    *
    * @return an HBox containing the controllers.
    */
   public HBox getColorController() {
      if (colorController == null) {
         colorController = new HBox();
         colorController.getChildren().add(primaryColor.getColorPicker());
         colorController.getChildren().add(secondaryColor.getColorPicker());
      }
      return colorController;
   }

   private void setCurrentColorPack(ColorPack cp) {
      currentColor.getColorPicker().getStyleClass().remove("active");
      currentColor = cp;
      currentColor.getColorPicker().getStyleClass().add("active");
   }

   /**
    * Private intern class that represent a color being tracked. It manages a
    * current color and defines controllers element to change its state.
    */
   private class ColorPack {

      private Color color;
      private ColorPicker cp;

      /**
       * Constructor. Define the color from the parameter, and create the
       * ColorPicker instance that will allow users to select colors.
       *
       * @param color
       */
      public ColorPack(Color color) {
         this.color = color;

         // Create the linked ColorPicker
         cp = new ColorPicker();
         cp.setValue(color);
         cp.setPrefWidth(50);
         cp.setPrefHeight(50);
         cp.getStyleClass().add("button");
         cp.setOnAction(new EventHandler() {
            @Override
            public void handle(Event t) {
               // Chnage the current ColorPack color
               ColorPack.this.setColor(cp.getValue());
               setCurrentColorPack(ColorPack.this);
            }
         });
         cp.addEventHandler(MouseEvent.ANY, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {

               // Get mouse position
               Point3D p = new Point3D(event.getX(), event.getY(), 0);

               if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
                  setCurrentColorPack(ColorPack.this);
               } else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
               } else if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
               }
            }
         });
      }

      /**
       * get the current color.
       *
       * @return the current color
       */
      private Color getColor() {
         return color;
      }

      /**
       * Set the color
       *
       * @param color the new color
       */
      private void setColor(Color color) {
         this.color = color;
      }

      /**
       * Get the ColorPack ColorPicker Return the ColorPicker instance that
       * manages the ColorPack
       */
      private ColorPicker getColorPicker() {
         return cp;
      }

   }
}
