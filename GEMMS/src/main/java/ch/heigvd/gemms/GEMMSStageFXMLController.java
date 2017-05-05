package ch.heigvd.gemms;

import ch.heigvd.layer.GEMMSText;
import ch.heigvd.workspace.Workspace;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import ch.heigvd.layer.GEMMSCanvas;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class GEMMSStageFXMLController implements Initializable {
    
    // Stage from main
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
    private BorderPane interfaceBorderPane;
    
    
    
    @FXML
    private GridPane layerController;
    
    
    /**
     * Button
     */
    @FXML
    private Button newDocumentButton;
    
    @FXML
    private Button openDocumentButton;
    
    @FXML
    private Button saveDocumentButton;
    

    private Document document;
    
    private Workspace workspace;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Create the first tools buttons row
        gridCreationTools.getRowConstraints().add(new RowConstraints(Constants.BUTTONS_HEIGHT));
        gridDrawingTools.getRowConstraints().add(new RowConstraints(Constants.BUTTONS_HEIGHT));
        gridColorTools.getRowConstraints().add(new RowConstraints(Constants.BUTTONS_HEIGHT));
        gridFilterTools.getRowConstraints().add(new RowConstraints(Constants.BUTTONS_HEIGHT));
        gridModificationTools.getRowConstraints().add(new RowConstraints(Constants.BUTTONS_HEIGHT));

        
        // Create a new document
        document = new Document(stage);
        
        
        // Create a new workspace
        newDocumentButton.setOnAction((ActionEvent e) -> {
            workspace = document.newDocument();
            interfaceBorderPane.setCenter(workspace);
            layerController.getChildren().add(workspace.getWorkspaceController());
            
            
            // Temporary button to create a Text Layer
            createToolButton("T+", gridCreationTools).setOnAction(event -> workspace.addLayer(new GEMMSText(50, 50, "Ceci est un texte"))); // pour appeler maFonction(), faire event->maFonction()
            createToolButton("C+", gridCreationTools).setOnAction(event -> workspace.addLayer(new GEMMSCanvas(workspace.width(), workspace.height()))); // pour appeler maFonction(), faire event->maFonction()
        });
        
        
        // Open workspace with file
        openDocumentButton.setOnAction((ActionEvent e) -> {
            try {
                workspace = document.open();
                interfaceBorderPane.setCenter(workspace);
                layerController.getChildren().add(workspace.getWorkspaceController());
            } catch (IOException | ClassNotFoundException ex) {
                Logger.getLogger(GEMMSStageFXMLController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        // Save a workspace
        saveDocumentButton.setOnAction((ActionEvent e) -> {
            try {
                document.save(workspace);
            } catch (IOException ex) {
                Logger.getLogger(GEMMSStageFXMLController.class.getName()).log(Level.SEVERE, null, ex);
            }

        });
        
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

        return button;
    }
    
    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
