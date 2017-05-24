package ch.heigvd.gemms;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class WelcomeInvite extends VBox {
   
   public WelcomeInvite(Label label, Button button) {
      setBackground(new Background(new BackgroundFill(Color.web("#ededed"), new CornerRadii(5), Insets.EMPTY)));
      setPadding(new Insets(15, 15, 25, 15));
      setAlignment(Pos.CENTER);
      label.setFont(Font.font(label.getFont().getFamily(), 18));
      label.setPadding(new Insets(20, 30, 20, 30));
      label.setTextAlignment(TextAlignment.CENTER);
      label.setWrapText(true);
      getChildren().add(label);
      button.getStyleClass().add("welcome-button");
      button.setPrefSize(100, 80);
      getChildren().add(button);
      setStyle("-fx-effect: dropshadow( gaussian , rgba(0,0,0,0.15) , 3 ,0 , 3 , 3 );");
   }
}
