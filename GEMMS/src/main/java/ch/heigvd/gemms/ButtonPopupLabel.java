/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.heigvd.gemms;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Popup;
import javafx.stage.Stage;

/**
 *
 * @author mathieu
 */
public class ButtonPopupLabel extends Popup {
   
   private Button target;
   
   public ButtonPopupLabel(String text) {
      
      // Set HBox container
      final HBox hbox = new HBox();
      hbox.setAlignment(Pos.CENTER);
      hbox.setBackground(new Background(new BackgroundFill(Color.web("#cdcdcd"), CornerRadii.EMPTY, Insets.EMPTY)));
      
      // Set padding and alignement
      hbox.setPadding(new Insets(5, 5, 5, 5));
      hbox.setAlignment(Pos.CENTER);
      
      hbox.setStyle("-fx-effect: dropshadow( gaussian , rgba(0,0,0,0.1) , 1 ,0 , 2 , 2 );");
      
      // Add elements
      hbox.getChildren().add(new Label(text));
      
      getContent().add(hbox);
   }
}
