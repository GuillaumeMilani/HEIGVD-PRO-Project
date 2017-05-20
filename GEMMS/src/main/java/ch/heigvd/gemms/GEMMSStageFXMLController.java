package ch.heigvd.gemms;

import ch.heigvd.dialog.ImportImageDialog;
import ch.heigvd.dialog.NewDocumentDialog;
import ch.heigvd.dialog.OpenDocumentDialog;
import ch.heigvd.dialog.ResizeDialog;
import ch.heigvd.layer.GEMMSText;
import ch.heigvd.layer.IGEMMSNode;
import ch.heigvd.tool.Drag;
import ch.heigvd.tool.settings.ToolColorSettings;
import ch.heigvd.workspace.Workspace;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.*;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import ch.heigvd.layer.GEMMSCanvas;
import ch.heigvd.layer.GEMMSImage;
import ch.heigvd.tool.Brush;
import ch.heigvd.tool.ColorSet;
import ch.heigvd.tool.Eraser;
import ch.heigvd.tool.EyeDropper;
import ch.heigvd.tool.Selection;
import ch.heigvd.tool.TextTool;
import java.util.List;
import ch.heigvd.tool.settings.ToolSettingsContainer;
import ch.heigvd.tool.settings.ToolSizeSettings;
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
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Pair;

public class GEMMSStageFXMLController implements Initializable {

    // Stage from main
    private Stage stage;

    @FXML
    private AnchorPane mainAnchorPane;

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
        ToolSizeSettings textSizer = new ToolSizeSettings(1, 300, GEMMSText.DEFAULT_SIZE);
        Button textCreation = createToolButton("", gridCreationTools);
        textCreation.getStyleClass().add(CSSIcons.TEXT_CREATION);
        textCreation.setOnAction(e -> {
           Workspace w = getCurrentWorkspace();
            if(w != null) {
               Optional<String> result = TextTool.getDialogText(null);
               if (result.isPresent()) {
                  GEMMSText t = new GEMMSText(w.width()/2, w.height()/2, result.get());
                  t.setFill(ColorSet.getInstance().getColor());
                  t.setFontSize(textSizer.getSize());
                  w.addLayer(t);
               }
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
        Button hSym = createToolButton("", gridModificationTools);
        hSym.getStyleClass().add(CSSIcons.H_SYMMETRY);
        hSym.setOnAction((ActionEvent e) -> {
            Workspace w = getCurrentWorkspace();
            if(w != null) {
                for (Node node : w.getCurrentLayers()) {
                    node.getTransforms().add(new Rotate(180,node.getBoundsInParent().getWidth()/2,node.getBoundsInParent().getHeight()/2,0,Rotate.Y_AXIS));
                }
            }
        });

        // Create symetrie vertical button action
        Button vSym = createToolButton("", gridModificationTools);
        vSym.getStyleClass().add(CSSIcons.V_SYMMETRY);
        vSym.setOnAction((ActionEvent e) -> {
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
        Button eraser = createToolButton("", gridDrawingTools);
        eraser.getStyleClass().add(CSSIcons.ERASER);
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
        
        // Create EyeDropper tool
        Button eyeDropper = createToolButton("", gridDrawingTools);
        eyeDropper.getStyleClass().add(CSSIcons.EYE_DROPPER);
        eyeDropper.setOnAction(e -> {
           Workspace w = getCurrentWorkspace();
            if(w != null) {
                w.setCurrentTool(new EyeDropper(w));
            }
        });

        // Create selection button action
        createToolButton("Se", gridModificationTools).setOnAction((ActionEvent e) -> {
            Workspace w = getCurrentWorkspace();
            if(w != null) {
                w.setCurrentTool(new Selection(stage.getScene(), w));
            }
        });

        // Create drag button action
        createToolButton("Drag", gridModificationTools).setOnAction((ActionEvent e) -> {
            Workspace w = getCurrentWorkspace();
            if(w != null) {
                w.setCurrentTool(new Drag(w));
            }
        });

        // Create rotate button action
        createToolButton("Rotate", gridModificationTools).setOnAction((ActionEvent e) -> {
            Workspace w = getCurrentWorkspace();
            if(w != null) {
                w.setCurrentTool(new ch.heigvd.tool.Rotate(w));
            }
        });


        // Create resize button action
        createToolButton("Resize", gridModificationTools).setOnAction((ActionEvent e) -> {
            Workspace w = getCurrentWorkspace();
            if(w != null) {
                w.setCurrentTool(new ch.heigvd.tool.Resize(w));
            }
        });


        // Create text button action
        Button text = createToolButton("", gridModificationTools);
        
        final ToolColorSettings textColor = new ToolColorSettings(ColorSet.getInstance().getColor());
        final ToolSettingsContainer textSettings = new ToolSettingsContainer(textSizer, textColor);
        text.getStyleClass().add(CSSIcons.TEXT_TOOL);
        text.setOnAction((ActionEvent e) -> {
            Workspace w = getCurrentWorkspace();
            if(w != null) {
               TextTool t = new TextTool(w);
               w.setCurrentTool(t); 
               textSizer.setTarget(t);
               textColor.setTarget(t);
               displayToolSetting(text, textSettings);
            }
        });

        mainAnchorPane.setOnKeyPressed(keyEvent -> {
            // ---------- ESC ----------

            if (keyEvent.getCode().equals(KeyCode.ESCAPE)) {
                // Disable current tool
                getCurrentWorkspace().setCurrentTool(null);

            // ---------- DEL ----------

            } else if (keyEvent.getCode().equals(KeyCode.DELETE)) {
            // Drop the current selected layers
                getCurrentWorkspace().getCurrentLayers().forEach(n->getCurrentWorkspace().getLayers().remove(n));
            }
            // ---------- CTRL + C ----------
            if (Constants.CTRL_C.match(keyEvent)) {

                // In case there is a selection
                if (getCurrentWorkspace() != null && getCurrentWorkspace().getLayerTool() != null && getCurrentWorkspace().getCurrentTool() instanceof Selection) {

                    // Get the selection
                    Selection selection = (Selection)getCurrentWorkspace().getCurrentTool();
                    int selectionWidth = (int)(selection.getRectangle().getWidth());
                    int selectionHeight = (int)(selection.getRectangle().getHeight());

                    // Prepare the canvas to save the selection
                    GEMMSCanvas canvas = new GEMMSCanvas(getCurrentWorkspace().width(), getCurrentWorkspace().height());
                    SnapshotParameters param = new SnapshotParameters();
                    param.setFill(Color.TRANSPARENT);

                    PixelWriter pixelWriter = canvas.getGraphicsContext2D().getPixelWriter();

                    BufferedImage image = new BufferedImage(selectionWidth, selectionHeight, BufferedImage.TYPE_INT_ARGB);

                    // Snapshot each node selected
                    for (Node n : getCurrentWorkspace().getCurrentLayers()) {
                        /**
                         * The viewport (part of node that will be snapshoted) must be in the
                         * node that will be snapshoted parent's coordinate system.
                         * Passing by the scene coordinate we are able to convert to the node parent's coordinate system.
                         */
                        double posXWCoord = n.sceneToLocal(getCurrentWorkspace().getLayerTool().localToScene(selection.getRectangle().getBoundsInParent())).getMinX();
                        double posYWCoord = n.sceneToLocal(getCurrentWorkspace().getLayerTool().localToScene(selection.getRectangle().getBoundsInParent())).getMinY();

                        /**
                         * The viewport will define the part of the node that will be snapshoted (the selection actually)
                         */
                        param.setViewport(new Rectangle2D(
                                posXWCoord,
                                posYWCoord,
                                selectionWidth,
                                selectionHeight));

                        BufferedImage newImage = SwingFXUtils.fromFXImage(n.snapshot(param, null), null);

                        Graphics2D graphics = (Graphics2D) image.getGraphics();

                        graphics.setBackground(java.awt.Color.WHITE);
                        graphics.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

                        graphics.drawImage(newImage, 0, 0, null);
                    }

                    // Get a pixel reader
                    PixelReader pixelReader = SwingFXUtils.toFXImage(image, null).getPixelReader();

                    // Write the color of every pixel
                    for (int y = 0; y < selectionHeight; ++y) {
                        for (int x = 0; x < selectionWidth; ++x) {
                            Color c = pixelReader.getColor(x, y);
                            pixelWriter.setColor(
                                    x + (int) Math.round(selection.getRectangle().getBoundsInParent().getMinX()),
                                    y + (int) Math.round(selection.getRectangle().getBoundsInParent().getMinY()),
                                    c);
                        }
                    }

                    // Save the canvas to clipboard
                    saveNodesToClipboard(Arrays.asList(canvas));

                // No selection then copy the current layers
                } else if (getCurrentWorkspace() != null && getCurrentWorkspace().getCurrentLayers() != null) {

                    saveNodesToClipboard(getCurrentWorkspace().getCurrentLayers());
                }

            } else if (Constants.CTRL_V.match(keyEvent)) {
                for (Node n : getNodesFromClipboard()) {
                    getCurrentWorkspace().addLayer(n);
                }
            }
        });


    }

    /**
     * Copy a list of nodes in the clipboard
     * @param nodes the nodes to copy to clipboard
     */
    private void saveNodesToClipboard(List<Node> nodes) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent cc = new ClipboardContent();

        // Serialize each node
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(baos);

            List<IGEMMSNode> n = new LinkedList<>();
            nodes.forEach(node -> n.add((IGEMMSNode)node));
            out.writeObject(n);

            cc.putString(Base64.getEncoder().encodeToString(baos.toByteArray()));
            baos.close();
        } catch (Exception e) {
            // TODO: manage exceptions
            e.printStackTrace();
        }

        clipboard.setContent(cc);
    }

    /**
     * Retrieve a list of nodes from the clipboard
     * @return the list of nodes that was in the clipboard
     */
    private List<Node> getNodesFromClipboard() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        String serializedObject = clipboard.getString();

        // Deserialize the clipboard's content
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(Base64.getDecoder().decode(serializedObject));
            ObjectInputStream in = new ObjectInputStream(bais);
            return (List<Node>)in.readObject();
        } catch (Exception e) {
            // TODO: manage exceptions
            e.printStackTrace();
            return null;
        }
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
    
    
    @FXML
    private void resizeButtonAction(ActionEvent e) {
        Workspace w = getCurrentWorkspace();
        if(w != null) {
            
            ResizeDialog dialog = new ResizeDialog(w);
            
            Optional<Rectangle> result = dialog.showAndWait();

            if(result.isPresent()) {
                
                w.resizeCanvas((int)result.get().getWidth(),
                               (int)result.get().getHeight(),
                               (int)result.get().getX(), 
                               (int)result.get().getY());
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
