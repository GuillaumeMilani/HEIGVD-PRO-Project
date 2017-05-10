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
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import javafx.util.converter.IntegerStringConverter;

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
    
    
    // Contains all workspace (tab)
    @FXML
    private TabPane workspaces;
    
    
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
    
    @FXML
    private Button exportDocumentButton;
    
    // List of documents
    private ArrayList<Document> documents;
    
    // File chooser
    private FileChooser fileChooser;
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Create the first tools buttons row
        gridCreationTools.getRowConstraints().add(new RowConstraints(Constants.BUTTONS_HEIGHT));
        gridDrawingTools.getRowConstraints().add(new RowConstraints(Constants.BUTTONS_HEIGHT));
        gridColorTools.getRowConstraints().add(new RowConstraints(Constants.BUTTONS_HEIGHT));
        gridFilterTools.getRowConstraints().add(new RowConstraints(Constants.BUTTONS_HEIGHT));
        gridModificationTools.getRowConstraints().add(new RowConstraints(Constants.BUTTONS_HEIGHT));

        
        // Init file chooser
        fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        
        
        // Document list
        documents = new ArrayList<>();
        
        
        // Create a new document
        newDocumentButton.setOnAction((ActionEvent e) -> {
            
            // Display dialog
            Optional<Pair<Integer, Integer>> result = newDocumentDialog();
            
            // Dialog OK
            if(result.isPresent()) {
                
                // Create a new document
                Document document = new Document(stage, result.get().getKey(), result.get().getValue());
                
                // Get workspace
                Workspace w = document.workspace();
                
                // Clear
                layerController.getChildren().clear();
                layerController.getChildren().add(w.getWorkspaceController());

                // Create tab
                Tab tab = new Tab("untiled", w);
                workspaces.getTabs().add(tab);
                workspaces.getSelectionModel().select(tab);
                
                documents.add(document);
            }
        });
        
        
        // Open workspace with file
        openDocumentButton.setOnAction((ActionEvent e) -> {

            File f = openDocumentDialog();
            if(f != null) {
                Document document = null;
                try {
                    document = new Document(stage, f);
                } catch (IOException ex) {
                    Logger.getLogger(GEMMSStageFXMLController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(GEMMSStageFXMLController.class.getName()).log(Level.SEVERE, null, ex);
                }

                Workspace w = document.workspace();

                // Clear
                layerController.getChildren().clear();
                layerController.getChildren().add(w.getWorkspaceController());

                // Create tab
                Tab tab = new Tab(document.name(), w);
                workspaces.getTabs().add(tab);
                workspaces.getSelectionModel().select(tab);
                
                documents.add(document);
            }
        });
        
        
        // Save a workspace
        saveDocumentButton.setOnAction(e -> {

            if(workspaces.getTabs().size() > 0) {
                // Get current tab
                Tab tab = workspaces.getSelectionModel().getSelectedItem();
                Workspace w = (Workspace)tab.getContent();

                // Research document with workspace
                Document d = getDocument(w);

                // Save document
                if(d != null) {
                    try {
                        d.save();
                    } catch (IOException ex) {
                        Logger.getLogger(GEMMSStageFXMLController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
                // Set tab title
                tab.setText(d.name());
            }
        });
        
        
        // Export workspace
        exportDocumentButton.setOnAction(e -> {
            if(workspaces.getTabs().size() > 0) {
                // Get current tab
                Tab tab = workspaces.getSelectionModel().getSelectedItem();
                Workspace w = (Workspace)tab.getContent();

                // Research document with workspace
                Document d = getDocument(w);

                // export document as image
                if(d != null) {
                    try {
                        d.export();
                    } catch (IOException ex) {
                        Logger.getLogger(GEMMSStageFXMLController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        
        
        // Tab changed action
        workspaces.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Tab> ov, Tab t, Tab t1) -> {
            if(workspaces.getTabs().size() > 0) {
                Workspace w = (Workspace)workspaces.getSelectionModel().getSelectedItem().getContent();
                layerController.getChildren().clear();
                layerController.getChildren().add(w.getWorkspaceController());
            }
            // Suppress tab
            else {
                // Get workspace
                Workspace w = (Workspace)t.getContent();
                
                // Research document with workspace
                Document d = getDocument(w);
                documents.remove(d);
                
                // TODO : save when close ?
                
                // Clear
                layerController.getChildren().clear();
            }
        });
        
        
        // Create text button action
        createToolButton("T+", gridCreationTools).setOnAction(e -> {
            if(workspaces.getTabs().size() > 0) {
                Workspace w = (Workspace)workspaces.getSelectionModel().getSelectedItem().getContent();
                w.addLayer(new GEMMSText(50, 50, "Ceci est un texte"));
            }
        });

        // Create canvas button action
        createToolButton("C+", gridCreationTools).setOnAction(e -> {
            if(workspaces.getTabs().size() > 0) {
                Workspace w = (Workspace)workspaces.getSelectionModel().getSelectedItem().getContent();
                w.addLayer(new GEMMSCanvas(w.width(), w.height()));
            }
        });



        // Create symetrie horizontal button action
        createToolButton("Sym hori", gridModificationTools).setOnAction((ActionEvent e) -> {
            if(workspaces.getTabs().size() > 0) {
                Workspace w = (Workspace)workspaces.getSelectionModel().getSelectedItem().getContent();
                for (Node node : w.getCurrentLayers()) {
                    node.getTransforms().add(new Rotate(180,node.getBoundsInParent().getWidth(),node.getBoundsInParent().getHeight(),0,Rotate.Y_AXIS));
                }
            }
        });

        // Create symetrie vertical button action
        createToolButton("Sym vert", gridModificationTools).setOnAction((ActionEvent e) -> {
            if(workspaces.getTabs().size() > 0) {
                Workspace w = (Workspace)workspaces.getSelectionModel().getSelectedItem().getContent();
                for (Node node : w.getCurrentLayers()) {
                    node.getTransforms().add(new Rotate(180,node.getBoundsInParent().getHeight(),node.getBoundsInParent().getWidth(),0,Rotate.X_AXIS));
                }
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
    
    
    /**
     * Set the main stage
     * 
     * @param stage stage to set
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    
    /**
     * Display a dialog that init a new document
     * 
     * @return Pair of width and height
     */
    private Optional<Pair<Integer, Integer>> newDocumentDialog() {
        // Create dialog
        Dialog<Pair<Integer, Integer>> dialog = new Dialog<>();
        dialog.setTitle("Create a new file");

        // Set button
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Set text field
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField width = new TextField();
        width.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));
        TextField height = new TextField();
        height.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));

        Label description = new Label("Document size");
        description.setFont(Font.font(null, FontWeight.BOLD, 13));
        grid.add(description, 0, 0);
        grid.add(new Label("Width:"), 0, 1);
        grid.add(width, 1, 1);
        grid.add(new Label("Height:"), 0, 2);
        grid.add(height, 1, 2);

        dialog.getDialogPane().setContent(grid);

        // Request focus
        Platform.runLater(() -> width.requestFocus());

        Node loginButton = dialog.getDialogPane().lookupButton(ButtonType.OK);
        loginButton.setDisable(true);

        // Field validation
        width.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty());
        });

        height.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty());
        });

        // Return result
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return new Pair<>(Integer.valueOf(width.getText()), Integer.valueOf(height.getText()));
            }

            return null;
        });

        return dialog.showAndWait();
    }
    
    
    /**
     * Display a dialog that allow to choose project file
     * 
     * @return File project
     */
    private File openDocumentDialog() {
        fileChooser.setTitle("Open file");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("GEMMS", "*.gemms"));

        return fileChooser.showOpenDialog(stage);
    }
    
    
    /**
     * Get document that contain the workspace
     * 
     * @param w workspace to find
     * @return Document that contain the workspace
     */
    private Document getDocument(Workspace w) {
        for(Document d : documents) {
            if(d.workspace() == w) {
               return d;
            }
        }
        
        return null;
    }
}
