/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.heigvd.gemms;

import javafx.scene.ImageCursor;
import javafx.scene.image.Image;

/**
 *
 * @author mathieu
 */
public class GEMMSCursors {
   
   
   public static ImageCursor createBrushCursor(int size) {
      Image image = new Image("img/brush-cursor.png", size, size, true, false);
      return new ImageCursor(image, image.getWidth()/2, image.getHeight()/2);
   }
}
