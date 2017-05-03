package ch.heigvd.gemms;

import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Popup;
import javafx.stage.Stage;

import javax.swing.*;

public class GEMMSStageFXMLController implements Initializable {
    
    private Scene scene;
    private Stage stage;

    /**
     * GridPanes containing the tools buttons
     */
    @FXML
    private GridPane gridDrawingTools;
    @FXML
    private GridPane gridColorTools;
    @FXML
    private GridPane gridFilterTools;
    @FXML
    private GridPane gridModificationTools;
    
    /**
     * AnchorPane containing the workspace
     */
    @FXML
    private AnchorPane centerAnchor;


    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Create the first tools buttons row
        gridDrawingTools.getRowConstraints().add(new RowConstraints(Constants.BUTTONS_HEIGHT));
        gridColorTools.getRowConstraints().add(new RowConstraints(Constants.BUTTONS_HEIGHT));
        gridFilterTools.getRowConstraints().add(new RowConstraints(Constants.BUTTONS_HEIGHT));
        gridModificationTools.getRowConstraints().add(new RowConstraints(Constants.BUTTONS_HEIGHT));

        // Create some buttons
        createToolButton("Example", gridDrawingTools).setOnAction(event -> {}); // pour appeler maFonction(), faire event->maFonction()

        // Workspace
        centerAnchor.setClip(new Rectangle(centerAnchor.getPrefWidth(), centerAnchor.getPrefHeight()));
    }

    /**
     * Create a tool button and add it in the corresponding grid pane
     * @param text  text of the button
     * @param pane  grid pane to add the button
     * @return the button created
     */
    // TODO: replace text by an image
    private Button createToolButton(String text, GridPane pane) {
        Button button = new Button(text);

        // Calculate the button's position in the grid
        int row = pane.getChildren().size() / 3;
        int col = pane.getChildren().size() % 3;

        // Add a buttons row if needed
        if (row > pane.getRowConstraints().size() - 1) {
            pane.getRowConstraints().add(new RowConstraints(Constants.BUTTONS_HEIGHT));
        }

        button.setPrefHeight(Double.MAX_VALUE);
        button.setPrefWidth(Double.MAX_VALUE);
        button.setPadding(new Insets(0, 0, 0, 0));

        pane.add(button, col, row);

        final Popup popup = new Popup();
        popup.setWidth(200);

        final HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER);
        hbox.setPrefHeight(40);
        hbox.setBackground(new Background(new BackgroundFill(Color.DARKGRAY, CornerRadii.EMPTY, Insets.EMPTY)));

        final ColorPicker cp = new ColorPicker();
        cp.setStyle("-fx-color-label-visible: false ;");

        final Slider sl = new Slider(0, 100, 50);

        hbox.getChildren().add(cp);
        hbox.getChildren().add(sl);

        popup.getContent().add(hbox);

        button.setOnMouseEntered(event -> {
            popup.show(stage);
            popup.setX(button.localToScreen(button.getBoundsInLocal()).getMinX());
            popup.setY(button.localToScreen(button.getBoundsInLocal()).getMaxY() - button.getBoundsInLocal().getHeight() / 3);
            popup.setAutoHide(true);
        });

        return button;
    }
    
    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public void setStage(Stage stage) { this.stage = stage; }
}
