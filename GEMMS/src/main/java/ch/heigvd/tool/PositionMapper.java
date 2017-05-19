/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.heigvd.tool;

import javafx.scene.canvas.Canvas;

/**
 *
 * @author mathieu
 */
public class PositionMapper {

   public static int mapX(int x, Canvas canvas) {
      return x - (int)canvas.getLayoutX() - (int)canvas.getTranslateX();
   }

   public static int mapY(int y, Canvas canvas) {
      return y - (int)canvas.getLayoutY() - (int)canvas.getTranslateY();
   }
}
