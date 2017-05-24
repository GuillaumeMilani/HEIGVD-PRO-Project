package ch.heigvd.gemms;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class ButtonPopupLabel extends HBox {

   public ButtonPopupLabel(String text) {
      
      // Set HBox container
      setAlignment(Pos.CENTER);
      setBackground(new Background(new BackgroundFill(Color.web("#cdcdcd"), CornerRadii.EMPTY, Insets.EMPTY)));
      
      // Set padding and alignement
      setPadding(new Insets(5, 5, 5, 5));
      setAlignment(Pos.CENTER);
      
      setStyle("-fx-effect: dropshadow( gaussian , rgba(0,0,0,0.25) , 1 ,0 , 1 , 1 );");
      
      // Add elements
      getChildren().add(new Label(text));
   }
}
