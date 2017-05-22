package ch.heigvd.gemms;

import ch.heigvd.dialog.ImportImageDialog;
import ch.heigvd.dialog.NewDocument;
import ch.heigvd.dialog.NewDocumentDialog;
import ch.heigvd.dialog.OpenDocumentDialog;
import ch.heigvd.dialog.ResizeDialog;

import ch.heigvd.layer.GEMMSText;
import ch.heigvd.layer.GEMMSCanvas;
import ch.heigvd.layer.IGEMMSNode;
import ch.heigvd.layer.GEMMSImage;

import ch.heigvd.tool.*;
import ch.heigvd.tool.settings.ToolColorSettings;
import ch.heigvd.tool.settings.ToolFontSettings;
import ch.heigvd.tool.settings.ToolSettingsContainer;
import ch.heigvd.tool.settings.ToolSizeSettings;

import ch.heigvd.workspace.Workspace;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.SepiaTone;
import javafx.scene.image.Image;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.stage.Popup;
import javafx.stage.Stage;



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
    private GridPane gridFilterTools;
    @FXML
    private GridPane gridSliders;
    @FXML
    private GridPane gridModificationTools;
    
    
    // Contains all workspace (tab)
    @FXML
    private TabPane workspaces;
    
    
    @FXML
    private VBox layerController;
    
    @FXML
    private AnchorPane colorController;

    // List of documents
    private ArrayList<Document> documents;
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {

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
        final ToolColorSettings textColor = new ToolColorSettings(ColorSet.getInstance().getColor());
        final ToolFontSettings textFont = new ToolFontSettings(6, 300, GEMMSText.DEFAULT_SIZE);
        Button textCreation = createToolButton("", gridCreationTools);
        textCreation.getStyleClass().add(CSSIcons.TEXT_CREATION);
        textCreation.setOnAction(e -> {
           Workspace w = getCurrentWorkspace();
            if(w != null) {
               Optional<String> result = TextTool.getDialogText(null);
               if (result.isPresent()) {
                  GEMMSText t = new GEMMSText(w.width()/2, w.height()/2, result.get());
                  t.setFill(ColorSet.getInstance().getColor());
                  t.setFont(textFont.getFont());
                  //t.setTranslateX(-t.getBoundsInParent().getWidth() / 2);
                  w.addLayer(t);
               }
            }
        });
        
        // Create text button action
        Button text = createToolButton("", gridModificationTools);
        final ToolSettingsContainer textSettings = new ToolSettingsContainer(textColor, textFont);
        text.getStyleClass().add(CSSIcons.TEXT_TOOL);
        text.setOnAction((ActionEvent e) -> {
            Workspace w = getCurrentWorkspace();
            if(w != null) {
               TextTool t = new TextTool(w);
               w.setCurrentTool(t); 
               textColor.setTarget(t);
               textFont.setTarget(t);
               displayToolSetting(text, textSettings);
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
                    i.setViewport(new Rectangle2D(0, 0, image.getWidth(), image.getHeight()));
                    w.addLayer(i);
                }
            }
        });

        // Create symetrie horizontal button action
        Button hSym = createToolButton("", gridModificationTools);
        hSym.getStyleClass().add(CSSIcons.H_SYMMETRY);
        hSym.setOnAction((ActionEvent e) -> {
            Workspace w = getCurrentWorkspace();
           if (w != null) {
              for (Node node : w.getCurrentLayers()) {
                 // If the node is a text, use the special formula for GEMMSTexts
                 if (node instanceof GEMMSText) {
                    GEMMSText t = (GEMMSText) node;
                    t.getTransforms().add(new Rotate(180, t.getX() + t.getBoundsInLocal().getWidth() / 2, t.getY() + t.getBoundsInLocal().getHeight() / 2, 0, Rotate.Y_AXIS));
                 } else {

                      node.getTransforms().add(new Rotate(180, node.getBoundsInLocal().getWidth() / 2, node.getBoundsInLocal().getHeight() / 2, 0, Rotate.Y_AXIS));
                 }
              }
           }
        });

        // Create symetrie vertical button action
        Button vSym = createToolButton("", gridModificationTools);
        vSym.getStyleClass().add(CSSIcons.V_SYMMETRY);
        vSym.setOnAction((ActionEvent e) -> {
           Workspace w = getCurrentWorkspace();
           if (w != null) {
              // If the node is a text, use the special formula for GEMMSTexts
             for (Node node : w.getCurrentLayers()) {

                 if (node instanceof GEMMSText) {
                    GEMMSText t = (GEMMSText) node;
                    t.getTransforms().add(new Rotate(180, t.getX() + t.getBoundsInLocal().getWidth() / 2, t.getY() + t.getBoundsInLocal().getHeight() / 2, 0, Rotate.X_AXIS));

                 } else {
                    node.getTransforms().add(new Rotate(180, node.getBoundsInLocal().getWidth() / 2, node.getBoundsInLocal().getHeight() / 2, 0, Rotate.X_AXIS));
                 }
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

        // Create bucket tool
        Button bucket = createToolButton("", gridDrawingTools);
        bucket.getStyleClass().add(CSSIcons.BUCKET);
        bucket.setOnAction(e -> {
            Workspace w = getCurrentWorkspace();
            if(w != null) {
                BucketFill b = new BucketFill(w);
                w.setCurrentTool(b);
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

        // Create drag button action
        Button drag = createToolButton("", gridModificationTools);
        drag.getStyleClass().add(CSSIcons.TRANSLATE);
        drag.setOnAction((ActionEvent e) -> {
            Workspace w = getCurrentWorkspace();
            if(w != null) {
                w.setCurrentTool(new Drag(w));
            }
        });


        // Create rotate button action
        Button rotate = createToolButton("", gridModificationTools);
        rotate.getStyleClass().add(CSSIcons.ROTATE);
        rotate.setOnAction((ActionEvent e) -> {
            Workspace w = getCurrentWorkspace();
            if(w != null) {
                w.setCurrentTool(new ch.heigvd.tool.RotateTool(w));
            }
        });

        // Create resize button action
        Button resize = createToolButton("", gridModificationTools);
        resize.getStyleClass().add(CSSIcons.SCALE);
        resize.setOnAction((ActionEvent e) -> {
            Workspace w = getCurrentWorkspace();
            if(w != null) {
                w.setCurrentTool(new ch.heigvd.tool.Resize(w));
            }
        });

        // Create selection button action
        Button selectionButton = createToolButton("", gridModificationTools);
        selectionButton.getStyleClass().add(CSSIcons.SELECTION);
        selectionButton.setOnAction((ActionEvent e) -> {
            Workspace w = getCurrentWorkspace();
            if(w != null) {
                w.setCurrentTool(new Selection(w));
            }
        });

        // Create crop button action
        Button crop = createToolButton("", gridModificationTools);
        crop.getStyleClass().add(CSSIcons.CROP);
        crop.setOnAction((ActionEvent e) -> {
            Workspace w = getCurrentWorkspace();
            if(w != null) {
                w.setCurrentTool(new Crop(w));
            }
        });
        
        
        
        //Create various sliders
        final Slider opacity = new Slider(0, 1, 1);
        final Slider sepia = new Slider(0, 1, 0);
        final Slider saturation = new Slider(-1, 1, 0);
        final Slider contrast = new Slider(-1, 1, 0);
        
        final Label opacityLabel = new Label("Opacity:");
        final Label sepiaLabel = new Label("Sepia:");
        final Label saturationLabel = new Label("Saturation:");
        final Label contrastLabel = new Label("Contrast:");
        
        final Label opacityValue = new Label(
                Double.toString(opacity.getValue()));
        final Label sepiaValue = new Label(
                Double.toString(sepia.getValue()));
        final Label saturationValue = new Label(
                Double.toString(saturation.getValue()));
        final Label contrastValue = new Label(
                Double.toString(contrast.getValue()));

        opacity.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                    Number old_val, Number new_val) {

                Workspace w = getCurrentWorkspace();
                if (w != null) {
                    for (Node n : w.getCurrentLayers()) {
                        n.setOpacity(new_val.doubleValue());
                    }
                }
                opacityValue.setText(String.format("%.2f", new_val));

            }
        });

        sepia.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                    Number old_val, Number new_val) {
                Workspace w = getCurrentWorkspace();
                if (w != null) {
                    for (Node n : w.getCurrentLayers()) {
                        ((SepiaTone) getColorAdjust(n).getInput()).setLevel(new_val.doubleValue());
                    }
                }
                sepiaValue.setText(String.format("%.2f", new_val));
            }
        });

        saturation.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                    Number old_val, Number new_val) {
                Workspace w = getCurrentWorkspace();
                if (w != null) {
                    for (Node n : w.getCurrentLayers()) {
                        getColorAdjust(n).setSaturation(new_val.doubleValue());
                    }
                }
                saturationValue.setText(String.format("%.2f", new_val));
            }
        });
        
        contrast.valueProperty().addListener(new ChangeListener<Number>() {
                public void changed(ObservableValue<? extends Number> ov,
                    Number old_val, Number new_val) {
                Workspace w = getCurrentWorkspace();
                if (w != null) {
                    for (Node n : w.getCurrentLayers()) {
                        getColorAdjust(n).setContrast(new_val.doubleValue());
                    }
                }
                contrastValue.setText(String.format("%.2f", new_val));
            }

        });

        gridSliders.setPadding(new Insets(10, 10, 10, 10));
        createSlider(gridSliders, opacityLabel, opacity, opacityValue, 1);
        createSlider(gridSliders, sepiaLabel, sepia, sepiaValue, 2);
        createSlider(gridSliders, saturationLabel, saturation, saturationValue, 3);
        createSlider(gridSliders, contrastLabel, contrast, contrastValue, 4);

        // Create filter button
        createToolButton("B&W", gridFilterTools).setOnAction((ActionEvent e) -> {
            Workspace w = getCurrentWorkspace();
            if (w != null) {
                for (Node n : w.getCurrentLayers()) {
                    getColorAdjust(n).setSaturation(-1);
                    saturation.setValue(-1);
                }
            }
        });
        
        // Create filter button
        createToolButton("Tint", gridFilterTools).setOnAction((ActionEvent e) -> {
            Workspace w = getCurrentWorkspace();
            if (w != null) {
                for (Node n : w.getCurrentLayers()) {
                    //Get hue between 0-360
                    double hue = ColorSet.getInstance().getColor().getHue();
                    //Add 180 and modulo 360 to get target colour
                    hue = (hue + 180) % 360;
                    //Map hue between -1 and 1
                    hue = -1 + 2 * (hue / 360);

                    //Finally, set the hue to node
                    getColorAdjust(n).setHue(hue);
                }
            }
        });
        
        // Create filter button
        createToolButton("Reset", gridFilterTools).setOnAction((ActionEvent e) -> {
            Workspace w = getCurrentWorkspace();
            if (w != null) {
                for (Node n : w.getCurrentLayers()) {
                    ColorAdjust c = getColorAdjust(n);
                    c.setHue(0);
                }
                opacity.setValue(1);
                saturation.setValue(0);
                sepia.setValue(0);
                contrast.setValue(0);
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
        Optional<NewDocument> result = dialog.showAndWait();

        // Dialog OK
        if(result.isPresent()) {

            int width = result.get().getWidth();
            int height = result.get().getHeiht();
            Color color = result.get().getColor();
            
            // Create a new document
            Document document = new Document(stage, width, height);

            // Get workspace
            Workspace w = document.workspace();
            
            GEMMSCanvas canvas = new GEMMSCanvas(width, height);
            GraphicsContext gc = canvas.getGraphicsContext2D();
            gc.setFill(color);
            gc.fillRect(0, 0, width, height);

            // Clear
            layerController.getChildren().clear();
            layerController.getChildren().add(w.getWorkspaceController());

            // Create tab
            Tab tab = new Tab("untitled", w);
            workspaces.getTabs().add(tab);
            workspaces.getSelectionModel().select(tab);
            
            // Set background
            w.addLayer(canvas);

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
    
    /**
     * Creates a slider in a pane at a certain position. Used to create opacity,
     * sepia, saturation and contrast sliders.
     * @param pane
     * @param label
     * @param slider
     * @param position
     */
    private void createSlider(GridPane pane, Label label, Slider slider, Label value, int position) {
        label.setMinWidth(50);
        value.setMinWidth(30);
        GridPane.setConstraints(label, 0, position);
        pane.getChildren().add(label);
        GridPane.setConstraints(slider, 1, position);
        pane.getChildren().add(slider);
        GridPane.setConstraints(value, 2, position);
        pane.getChildren().add(value);

    }
    
    /**
     * Returns node ColorAdjust effect. If it has none, creates one with
     * SepiaTone as input.
     * @param n Node
     * @return node's ColorAdjust
     */
    private ColorAdjust getColorAdjust(Node n){
        if(!(n.getEffect() instanceof ColorAdjust)){
            ColorAdjust c = new ColorAdjust();
            SepiaTone s = new SepiaTone(0);
            c.setInput(s);
            n.setEffect(c);
        }
        return ((ColorAdjust) n.getEffect());
    }
}
