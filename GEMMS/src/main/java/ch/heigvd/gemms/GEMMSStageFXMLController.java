package ch.heigvd.gemms;

import ch.heigvd.dialog.ImportImageDialog;
import ch.heigvd.dialog.NewDocumentDialog;
import ch.heigvd.dialog.OpenDocumentDialog;
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
import ch.heigvd.layer.GEMMSImage;
import ch.heigvd.tool.Brush;
import ch.heigvd.tool.ColorSet;
import ch.heigvd.tool.Eraser;
import ch.heigvd.tool.Selection;
import ch.heigvd.tool.ToolSettingsContainer;
import ch.heigvd.tool.ToolSizeSettings;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.transform.Rotate;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Pair;

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
    
    @FXML
    private AnchorPane colorController;

    // List of documents
    private ArrayList<Document> documents;
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Create the first tools buttons row
        gridCreationTools.getRowConstraints().add(new RowConstraints(Constants.BUTTONS_HEIGHT));
        gridDrawingTools.getRowConstraints().add(new RowConstraints(Constants.BUTTONS_HEIGHT));
        gridColorTools.getRowConstraints().add(new RowConstraints(Constants.BUTTONS_HEIGHT));
        gridFilterTools.getRowConstraints().add(new RowConstraints(Constants.BUTTONS_HEIGHT));
        gridModificationTools.getRowConstraints().add(new RowConstraints(Constants.BUTTONS_HEIGHT));
        // Document list
        documents = new ArrayList<>();
        
        
        // Tab changed action
        workspaces.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Tab> ov, Tab t, Tab t1) -> {
           Workspace w = getCurrentWorkspace();
           if(w != null) {
                layerController.getChildren().clear();
                layerController.getChildren().add(w.getWorkspaceController());
            }
            // Suppress tab
            else {
                // Get workspace
                w = (Workspace)t.getContent();
                
                // Research document with workspace
                Document d = getDocument(w);
                documents.remove(d);
                
                // TODO : save when close ?
                
                // Clear
                layerController.getChildren().clear();
            }
        });
        
        colorController.getChildren().add(ColorSet.getInstance().getColorController());
        
        // Create text button action
        Button textCreation = createToolButton("", gridCreationTools);
        textCreation.getStyleClass().add(CSSIcons.TEXT_CREATION);
        textCreation.setOnAction(e -> {
           Workspace w = getCurrentWorkspace();
            if(w != null) {
                w.addLayer(new GEMMSText(50, 50, "Ceci est un texte"));
            }
        });

        // Create canvas button action
        Button canvasCreation = createToolButton("", gridCreationTools);
        canvasCreation.getStyleClass().add(CSSIcons.CANVAS_CREATION);
        canvasCreation.setOnAction(e -> {
           Workspace w = getCurrentWorkspace();
            if(w != null) {
                w.addLayer(new GEMMSCanvas(w.width(), w.height()));  
            }
        });

        // Create image button action
        Button imageCreation = createToolButton("", gridCreationTools);
        imageCreation.getStyleClass().add(CSSIcons.IMAGE_CREATION);
        imageCreation.setOnAction(e -> {
           Workspace w = getCurrentWorkspace();
            if(w != null) {
                ImportImageDialog dialog = new ImportImageDialog(stage);
                Image image = dialog.showAndWait();
                if(image != null) {
                    GEMMSImage i = new GEMMSImage(image);
                    i.setViewport(new Rectangle2D(0, 0, w.width(), w.height()));
                    w.addLayer(i);
                }
            }
        });

        // Create symetrie horizontal button action
        createToolButton("Sym hori", gridModificationTools).setOnAction((ActionEvent e) -> {
            Workspace w = getCurrentWorkspace();
            if(w != null) {
                for (Node node : w.getCurrentLayers()) {
                    node.getTransforms().add(new Rotate(180,node.getBoundsInParent().getWidth()/2,node.getBoundsInParent().getHeight()/2,0,Rotate.Y_AXIS));
                }
            }
        });

        // Create symetrie vertical button action
        createToolButton("Sym vert", gridModificationTools).setOnAction((ActionEvent e) -> {
            Workspace w = getCurrentWorkspace();
            if(w != null) {
                for (Node node : w.getCurrentLayers()) {
                    node.getTransforms().add(new Rotate(180,node.getBoundsInParent().getWidth()/2,node.getBoundsInParent().getHeight()/2,0,Rotate.X_AXIS));
                }
            }
        });
        
        // Create brush tool
        Button brush = createToolButton("", gridDrawingTools);
        brush.getStyleClass().add(CSSIcons.BRUSH);
        ToolSizeSettings brushSizer = new ToolSizeSettings(1, 150, 5);
        final ToolSettingsContainer brushSettings = new ToolSettingsContainer(brushSizer);
        brush.setOnAction(e -> {
           Workspace w = getCurrentWorkspace();
            if(w != null) {
               Brush b = new Brush(w);
               w.setCurrentTool(b);
               brushSizer.setTarget(b);
               displayToolSetting(brush, brushSettings);
            }
        });

        // Create eraser tool
        Button eraser = createToolButton("Eraser", gridDrawingTools);
        ToolSizeSettings eraserSizer = new ToolSizeSettings(1, 150, 5);
        final ToolSettingsContainer eraserSettings = new ToolSettingsContainer(eraserSizer);
        eraser.setOnAction(e -> {
           Workspace w = getCurrentWorkspace();
            if(w != null) {
               Eraser er = new Eraser(w);
               w.setCurrentTool(er);
               eraserSizer.setTarget(er);
               displayToolSetting(eraser, eraserSettings);
            }
        });
        
        // Create selection button action
        createToolButton("Se", gridModificationTools).setOnAction((ActionEvent e) -> {
           Workspace w = getCurrentWorkspace();
            if(w != null) {
                w.setCurrentTool(new Selection(stage.getScene(), w));
            }
        });
    }
    
    private void displayToolSetting(Button button, Popup popup) {
      popup.show(stage);
      popup.setX(button.localToScreen(button.getBoundsInLocal()).getMinX());
      popup.setY(button.localToScreen(button.getBoundsInLocal()).getMaxY());
      popup.setAutoHide(true);
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
        button.getStyleClass().add("tool-button");

        pane.add(button, col, row);

        return button;
    }
    
    
    @FXML
    private void newButtonAction(ActionEvent e) {
        
        // Create a new dialog
        NewDocumentDialog dialog = new NewDocumentDialog();
        
        // Display dialog
        Optional<Pair<Integer, Integer>> result = dialog.showAndWait();

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
            Tab tab = new Tab("untitled", w);
            workspaces.getTabs().add(tab);
            workspaces.getSelectionModel().select(tab);

            documents.add(document);
        }
    }
    
    
    @FXML
    private void openButtonAction(ActionEvent e) {
        OpenDocumentDialog dialog = new OpenDocumentDialog(stage);
        
        File f = dialog.showAndWait();
        if(f != null) {
            Document document = null;
            try {
                document = new Document(stage, f);
            } catch (IOException | ClassNotFoundException ex) {
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
    }
    
    
    @FXML
    private void saveButtonAction(ActionEvent e) {
        Workspace w = getCurrentWorkspace();
        if(w != null) {
            // Get current tab
            Tab tab = workspaces.getSelectionModel().getSelectedItem();

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
    }
    
    
    @FXML
    private void exportButtonAction(ActionEvent e) {
        Workspace w = getCurrentWorkspace();
        if(w != null) {
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
     * Get the current workspace displayed
     * 
     * @return current workspace
     */
    private Workspace getCurrentWorkspace() {
        if (workspaces.getTabs().size() > 0) {
            return (Workspace) workspaces.getSelectionModel().getSelectedItem().getContent();
        }
        
        return null;
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
