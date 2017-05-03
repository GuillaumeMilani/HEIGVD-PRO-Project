package ch.heigvd.gemms;

import ch.heigvd.layer.GEMMSText;
import ch.heigvd.workspace.Workspace;
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
import ch.heigvd.layer.GEMMSCanvas;

public class GEMMSStageFXMLController implements Initializable {
    
    private Scene scene;
    private Stage stage;

    /**
     * GridPanes containing the tools buttons
     */
    @FXML
    private GridPane gridCreationTools;
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
    
    @FXML
    private GridPane layerController;
    
    private Workspace workspace;


    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Create the first tools buttons row
        gridCreationTools.getRowConstraints().add(new RowConstraints(Constants.BUTTONS_HEIGHT));
        gridDrawingTools.getRowConstraints().add(new RowConstraints(Constants.BUTTONS_HEIGHT));
        gridColorTools.getRowConstraints().add(new RowConstraints(Constants.BUTTONS_HEIGHT));
        gridFilterTools.getRowConstraints().add(new RowConstraints(Constants.BUTTONS_HEIGHT));
        gridModificationTools.getRowConstraints().add(new RowConstraints(Constants.BUTTONS_HEIGHT));

        // Create some buttons
        createToolButton("Example", gridDrawingTools).setOnAction(event -> {}); // pour appeler maFonction(), faire event->maFonction()

        // Workspace Pane container
        // /!\ Set in code, to change later /!\
        centerAnchor.setPrefSize(600, 700);
        centerAnchor.setClip(new Rectangle(centerAnchor.getPrefWidth(), centerAnchor.getPrefHeight()));
        centerAnchor.setId("workspaceAnchorPane"); // Set id for CSS styling
        
        // Create the Workspace with hardcoded dimensions, to change later
        workspace = new Workspace(500, 500, centerAnchor);
//        AnchorPane.setTopAnchor(centerAnchor, 5.0);
//        AnchorPane.setBottomAnchor(centerAnchor, 5.0);
//        AnchorPane.setRightAnchor(centerAnchor, 5.0);
//        AnchorPane.setLeftAnchor(centerAnchor, 5.0);
        centerAnchor.getChildren().add(workspace);
        
        // Temporary button to create a Text Layer
        createToolButton("T+", gridCreationTools).setOnAction(event -> workspace.addLayer(new GEMMSText(50, 50, "Ceci est un texte"))); // pour appeler maFonction(), faire event->maFonction()
        createToolButton("C+", gridCreationTools).setOnAction(event -> workspace.addLayer(new GEMMSCanvas(workspace.getPrefWidth(), workspace.getPrefHeight()))); // pour appeler maFonction(), faire event->maFonction()

        
        layerController.getChildren().add(workspace.getWorkspaceController());
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
