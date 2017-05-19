/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.heigvd.tool.settings;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
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
public class ToolSettingsContainer extends Popup {
   public ToolSettingsContainer(Node... settings) {
      // Set HBox container
      final HBox hbox = new HBox();
      hbox.setAlignment(Pos.CENTER);
      hbox.setBackground(new Background(new BackgroundFill(Color.DARKGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
      
      hbox.setPadding(new Insets(5, 5, 5, 5));
      hbox.setSpacing(10);
      
      for(Node s : settings) {
         //s.setAlignment(Pos.CENTER_LEFT);
         hbox.getChildren().add(s);
      }
      
      getContent().add(hbox);
   }
}
