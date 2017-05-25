package ch.heigvd.gemms;

import ch.heigvd.dialog.ImportImageDialog;
import ch.heigvd.dialog.NewDocument;
import ch.heigvd.dialog.NewDocumentDialog;
import ch.heigvd.dialog.OpenDocumentDialog;
import ch.heigvd.dialog.ResizeDialog;

import ch.heigvd.layer.GEMMSText;
import ch.heigvd.layer.GEMMSCanvas;
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
import javafx.event.*;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;



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
    
    @FXML
    private HBox toolSettingsContainer;

    // List of documents
    private ArrayList<Document> documents;
    
    // Tab to wlecom users and invite to click
    private Tab welcomeTab;
    
    // List of created tool buttons
    LinkedList<Button> toolButtons = new LinkedList();
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Document list
        documents = new ArrayList<>();
        
        // Add a welcom panel 
        welcomeTab = new Tab();
        StackPane welcomeContainer = new StackPane(); // Container to center
        GridPane welcomeGrid = new GridPane(); // Grid for multiple panels
        
        // Button for new document invite
        Button newButtonInvite = new Button();
        newButtonInvite.getStyleClass().add("new-document-button");
        newButtonInvite.setOnAction(e -> {
           newButtonAction(e);
        });
        WelcomeInvite newInvite = new WelcomeInvite(new Label("Créer un nouveau document."), newButtonInvite);
        
        // Button for open document invite
        Button openButtonInvite = new Button();
        openButtonInvite.getStyleClass().add("open-document-button");
        openButtonInvite.setOnAction(e -> {
           openButtonAction(e);
        });
        WelcomeInvite openInvite = new WelcomeInvite(new Label("Ouvrir un document GEMMS."), openButtonInvite);
        
        // Add invites
        welcomeGrid.add(newInvite, 0, 0);
        welcomeGrid.add(openInvite, 1, 0);
        
        // Set visual parameters
        welcomeGrid.setHgap(15); 
        welcomeGrid.setVgap(15);
        welcomeGrid.setMaxSize(460, 460);
        
        // Add grid to the container
        welcomeContainer.getChildren().add(welcomeGrid);
        StackPane.setAlignment(welcomeGrid, Pos.CENTER);
        
        // Welcome tab parameters
        welcomeTab.setContent(welcomeContainer);
        welcomeTab.setText("Welcome !");
        workspaces.getTabs().add(welcomeTab);
        
               // Register scroll event for zoom
       workspaces.setOnScroll(new EventHandler<ScrollEvent>() {
          @Override
          public void handle(ScrollEvent event) {
             Workspace workspace = getCurrentWorkspace();
             if (workspace != null) {
                if (event.isControlDown()) {
                   if (event.getDeltaY() > 0) {
                      workspace.zoom(1.05);
                   } else {
                      workspace.zoom(0.95);
                   }
                }
             }
          }
       });

       EventHandler dragEventHandler = new EventHandler<MouseEvent>() {
          private double x;
          private double y;

          @Override
          public void handle(MouseEvent event) {
             Workspace workspace = getCurrentWorkspace();
             if (workspace != null) {
                if (event.isShiftDown()) {
                   if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
                      x = event.getX();
                      y = event.getY();
                   } else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                      workspace.move(event.getX() - x, event.getY() - y);
                      x = event.getX();
                      y = event.getY();
                   }
                   event.consume();
                } else {
                   x = event.getX();
                   y = event.getY();
                }
             }
          }
       };
       workspaces.addEventFilter(MouseEvent.ANY, dragEventHandler);
       workspaces.addEventHandler(MouseEvent.ANY, dragEventHandler);


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
                
                
                  clearSelectedButtons();
                
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
        setHoverHint(textCreation, "Create a new Text.");
        textCreation.setOnAction(e -> {
           clearSelectedButtons();
           Workspace w = getCurrentWorkspace();
            if(w != null) {
               Optional<String> result = TextTool.getDialogText(null);
               if (result.isPresent()) {
                  GEMMSText t = new GEMMSText(w.width()/2, w.height()/2, result.get());
                  t.setFill(ColorSet.getInstance().getColor());
                  t.setFont(textFont.getFont());
                  t.setTranslateX(-t.getBoundsInParent().getWidth() / 2);
                  w.addLayer(t);
                  displayToolSetting(textCreation, null);
               }
               clearSelectedButtons();
            }
        });
        
        // Create text button action
        Button text = createToolButton("", gridModificationTools);
        final ToolSettingsContainer textSettings = new ToolSettingsContainer(textColor, textFont);
        text.getStyleClass().add(CSSIcons.TEXT_TOOL);
        setHoverHint(text, "Edit a text properties.");
        text.setOnAction((ActionEvent e) -> {
           clearSelectedButtons();
            Workspace w = getCurrentWorkspace();
            if(w != null) {
               selectButton(text);
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
        setHoverHint(canvasCreation, "Create a new painting canvas.");
        canvasCreation.setOnAction(e -> {
           clearSelectedButtons();
           Workspace w = getCurrentWorkspace();
            if(w != null) {
                w.addLayer(new GEMMSCanvas(w.width(), w.height()));
                displayToolSetting(canvasCreation, null);
            }
        });

        // Create image button action
        Button imageCreation = createToolButton("", gridCreationTools);
        imageCreation.getStyleClass().add(CSSIcons.IMAGE_CREATION);
        setHoverHint(imageCreation, "Import an image.");
        imageCreation.setOnAction(e -> {
           Workspace w = getCurrentWorkspace();
            if(w != null) {
                ImportImageDialog dialog = new ImportImageDialog(stage);
                Image image = dialog.showAndWait();
                if(image != null) {
                    GEMMSImage i = new GEMMSImage(image);
                    i.setViewport(new Rectangle2D(0, 0, image.getWidth(), image.getHeight()));
                    w.addLayer(i);
                    displayToolSetting(imageCreation, null);
                }
            }
        });

        // Create symetrie horizontal button action
        Button hSym = createToolButton("", gridModificationTools);
        hSym.getStyleClass().add(CSSIcons.H_SYMMETRY);
        setHoverHint(hSym, "Apply a horizontal symetry");
        hSym.setOnAction((ActionEvent e) -> {
            Workspace w = getCurrentWorkspace();
           if (w != null) {
              clearSelectedButtons();
              for (Node node : w.getCurrentLayers()) {
                 // If the node is a text, use the special formula for GEMMSTexts
                 if (node instanceof GEMMSText) {
                    GEMMSText t = (GEMMSText) node;
                    t.getTransforms().add(new Rotate(180, t.getX() + t.getBoundsInLocal().getWidth() / 2, t.getY() + t.getBoundsInLocal().getHeight() / 2, 0, Rotate.Y_AXIS));
                 } else {

                      node.getTransforms().add(new Rotate(180, node.getBoundsInLocal().getWidth() / 2, node.getBoundsInLocal().getHeight() / 2, 0, Rotate.Y_AXIS));
                 }
              }
              displayToolSetting(hSym, null);
           }
        });

        // Create symetrie vertical button action
        Button vSym = createToolButton("", gridModificationTools);
        vSym.getStyleClass().add(CSSIcons.V_SYMMETRY);
        setHoverHint(vSym, "Apply a vertical symetry");
        vSym.setOnAction((ActionEvent e) -> {
           Workspace w = getCurrentWorkspace();
           if (w != null) {
              clearSelectedButtons();
              // If the node is a text, use the special formula for GEMMSTexts
             for (Node node : w.getCurrentLayers()) {

                 if (node instanceof GEMMSText) {
                    GEMMSText t = (GEMMSText) node;
                    t.getTransforms().add(new Rotate(180, t.getX() + t.getBoundsInLocal().getWidth() / 2, t.getY() + t.getBoundsInLocal().getHeight() / 2, 0, Rotate.X_AXIS));

                 } else {
                    node.getTransforms().add(new Rotate(180, node.getBoundsInLocal().getWidth() / 2, node.getBoundsInLocal().getHeight() / 2, 0, Rotate.X_AXIS));
                 }
              }
              displayToolSetting(vSym, null);
           }
        });
        
        // Create brush tool
        Button brush = createToolButton("", gridDrawingTools);
        brush.getStyleClass().add(CSSIcons.BRUSH);
        setHoverHint(brush, "Paint on a canvas.");
        ToolSizeSettings brushSizer = new ToolSizeSettings(1, 150, 5);
        final ToolSettingsContainer brushSettings = new ToolSettingsContainer(brushSizer);
        brush.setOnAction(e -> {
           Workspace w = getCurrentWorkspace();
            if(w != null) {
               clearSelectedButtons();
               selectButton(brush);
               Brush b = new Brush(w);
               w.setCurrentTool(b);
               brushSizer.setTarget(b);
               displayToolSetting(brush, brushSettings);
            }
        });

        // Create eraser tool
        Button eraser = createToolButton("", gridDrawingTools);
        eraser.getStyleClass().add(CSSIcons.ERASER);
        setHoverHint(eraser, "Erase color from a canvas.");
        ToolSizeSettings eraserSizer = new ToolSizeSettings(1, 150, 5);
        final ToolSettingsContainer eraserSettings = new ToolSettingsContainer(eraserSizer);
        eraser.setOnAction(e -> {
           Workspace w = getCurrentWorkspace();
            if(w != null) {
               clearSelectedButtons();
               selectButton(eraser);
               Eraser er = new Eraser(w);
               w.setCurrentTool(er);
               eraserSizer.setTarget(er);
               displayToolSetting(eraser, eraserSettings);
            }
        });
        
        // Create EyeDropper tool
        Button eyeDropper = createToolButton("", gridDrawingTools);
        eyeDropper.getStyleClass().add(CSSIcons.EYE_DROPPER);
        setHoverHint(eyeDropper, "Pick a color on a layer.");
        eyeDropper.setOnAction(e -> {
           Workspace w = getCurrentWorkspace();
            if(w != null) {
               clearSelectedButtons();
               selectButton(eyeDropper);
                w.setCurrentTool(new EyeDropper(w));
              displayToolSetting(eyeDropper, null);
            }
        });

        // Create drag button action
        Button drag = createToolButton("", gridModificationTools);
        drag.getStyleClass().add(CSSIcons.TRANSLATE);
        setHoverHint(drag, "Move a layer around.");
        drag.setOnAction((ActionEvent e) -> {
            Workspace w = getCurrentWorkspace();
            if(w != null) {
               clearSelectedButtons();
               selectButton(drag);
                w.setCurrentTool(new Drag(w));
              displayToolSetting(drag, null);
            }
        });


        // Create rotate button action
        Button rotate = createToolButton("", gridModificationTools);
        rotate.getStyleClass().add(CSSIcons.ROTATE);
        setHoverHint(rotate, "Rotate a layer.");
        rotate.setOnAction((ActionEvent e) -> {
            Workspace w = getCurrentWorkspace();
            if(w != null) {
               clearSelectedButtons();
               selectButton(rotate);
                w.setCurrentTool(new ch.heigvd.tool.Rotate(w));
              displayToolSetting(rotate, null);
            }
        });

        // Create resize button action
        Button resize = createToolButton("", gridModificationTools);
        resize.getStyleClass().add(CSSIcons.SCALE);
        setHoverHint(resize, "Resize a layer.");
        resize.setOnAction((ActionEvent e) -> {
            Workspace w = getCurrentWorkspace();
            if(w != null) {
               clearSelectedButtons();
               selectButton(resize);
                w.setCurrentTool(new ch.heigvd.tool.Resize(w));
              displayToolSetting(resize, null);
            }
        });

        // Create selection button action
        Button selectionButton = createToolButton("", gridModificationTools);
        setHoverHint(selectionButton, "Select an area on the document.");
        selectionButton.getStyleClass().add(CSSIcons.SELECTION);
        selectionButton.setOnAction((ActionEvent e) -> {
            Workspace w = getCurrentWorkspace();
            if(w != null) {
               clearSelectedButtons();
               selectButton(selectionButton);
                w.setCurrentTool(new Selection(w));
              displayToolSetting(selectionButton, null);
            }
        });

        // Create crop button action
        Button crop = createToolButton("", gridModificationTools);
        setHoverHint(crop, "Crop the document.");
        crop.getStyleClass().add(CSSIcons.CROP);
        crop.setOnAction((ActionEvent e) -> {
            Workspace w = getCurrentWorkspace();
            if(w != null) {
               clearSelectedButtons();
               selectButton(crop);
                w.setCurrentTool(new Crop(w));
              displayToolSetting(crop, null);
            }
        });
        
        
        //Create various sliders
        final Slider opacity = new Slider(0, 1, 1);
        final Slider sepia = new Slider(0, 1, 0);
        final Slider saturation = new Slider(-1, 1, 0);
        final Slider contrast = new Slider(-1, 1, 0);
        final Slider brightness = new Slider(-1, 1, 0);
        final Slider blur = new Slider (0, 100, 0);
  
        //Create labels to show current slider value
        final Label opacityValue = new Label(
                Double.toString(opacity.getValue()));
        final Label sepiaValue = new Label(
                Double.toString(sepia.getValue()));
        final Label saturationValue = new Label(
                Double.toString(saturation.getValue()));
        final Label contrastValue = new Label(
                Double.toString(contrast.getValue()));
        final Label brightnessValue = new Label(
                Double.toString(brightness.getValue()));
        final Label blurValue = new Label(
                Double.toString(blur.getValue()));

        //Add a ChangeListener to each slider so that they may modify current layers
        opacity.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
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
            @Override
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
            @Override
            public void changed(ObservableValue<? extends Number> ov,
                    Number old_val, Number new_val) {
                Workspace w = getCurrentWorkspace();
                if (w != null) {
                    for (Node n : w.getCurrentLayers()) {
                        getColorAdjust(n).setSaturation(new_val.doubleValue());
                    }
                    w.notifyHistory();
                }
                saturationValue.setText(String.format("%.2f", new_val));
            }
        });
        
        contrast.valueProperty().addListener(new ChangeListener<Number>() {
                @Override
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
        
        brightness.valueProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> ov,
                    Number old_val, Number new_val) {
                Workspace w = getCurrentWorkspace();
                if (w != null) {
                    for (Node n : w.getCurrentLayers()) {
                        getColorAdjust(n).setBrightness(new_val.doubleValue());
                    }
                }
                brightnessValue.setText(String.format("%.2f", new_val));
            }
        });
        
        blur.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov,
                    Number old_val, Number new_val) {
                Workspace w = getCurrentWorkspace();
                if (w != null) {
                    for (Node n : w.getCurrentLayers()) {
                        setBlurRadius(n, new_val.intValue());
                    }
                }
                blurValue.setText(String.format("%d", new_val.intValue()));
            }
        });

        //
        EventHandler eh = new EventHandler() {
            @Override
            public void handle(Event event) {
                Workspace w = getCurrentWorkspace();
                if(w != null){
                    getCurrentWorkspace().notifyHistory();
                }
            }
        };

        opacity.setOnMouseReleased(eh);
        sepia.setOnMouseReleased(eh);
        saturation.setOnMouseReleased(eh);
        contrast.setOnMouseReleased(eh);
        brightness.setOnMouseReleased(eh);

        gridSliders.setPadding(new Insets(10, 10, 10, 10));
        createSlider(gridSliders, "Opacity:", opacity, opacityValue, 1);
        createSlider(gridSliders, "Sepia:", sepia, sepiaValue, 2);
        createSlider(gridSliders, "Saturation:", saturation, saturationValue, 3);
        createSlider(gridSliders, "Contrast:", contrast, contrastValue, 4);
        createSlider(gridSliders, "Brightness:", brightness, brightnessValue, 5);
        createSlider(gridSliders, "Blur", blur, blurValue, 6);

        // Create filter button
        Button BW = createToolButton("B&W", gridFilterTools);
        setHoverHint(BW, "Apply a Black and White filter.");
        BW.setOnAction((ActionEvent e) -> {
            Workspace w = getCurrentWorkspace();
            if (w != null) {
               clearSelectedButtons();
                for (Node n : w.getCurrentLayers()) {
                    getColorAdjust(n).setSaturation(-1);
                    saturation.setValue(-1);
                }
                w.notifyHistory();

                displayToolSetting(BW, null);
            }
        });
        
        // Create filter button
        Button tint = createToolButton("Tint", gridFilterTools);
        setHoverHint(tint, "Apply a Tint filter of the current color.");
        tint.setOnAction((ActionEvent e) -> {
            Workspace w = getCurrentWorkspace();
            if (w != null) {
               clearSelectedButtons();
                for (Node n : w.getCurrentLayers()) {
                    //Algorithm to convert color to hue:
                    //https://stackoverflow.com/questions/31587092
                    //Get hue between 0-360
                    double hue = ColorSet.getInstance().getColor().getHue();
                    //Add 180 and modulo 360 to get target colour
                    hue = (hue + 180) % 360;
                    //Map hue between -1 and 1
                    hue = -1 + 2 * (hue / 360);

                    //Finally, set the hue to node
                    getColorAdjust(n).setHue(hue);
                }
                w.notifyHistory();

                displayToolSetting(tint, null);
            }
        });

        
        // Create filter button
        Button reset = createToolButton("Reset", gridFilterTools);
        setHoverHint(reset, "Reset all color effects.");
        reset.setOnAction((ActionEvent e) -> {
           clearSelectedButtons();
            Workspace w = getCurrentWorkspace();
            if (w != null) {
                for (Node n : w.getCurrentLayers()) {
                    ColorAdjust c = getColorAdjust(n);
                    c.setHue(0);
                    setBlurRadius(n, 0);
                }
                w.notifyHistory();

                opacity.setValue(1);
                saturation.setValue(0);
                sepia.setValue(0);
                contrast.setValue(0);
                brightness.setValue(0);
                
                displayToolSetting(reset, null);
            }
        });

        mainAnchorPane.setOnKeyPressed(keyEvent -> {
            // ---------- ESC ----------

            if (keyEvent.getCode().equals(KeyCode.ESCAPE)) {
                // Disable current tool
                getCurrentWorkspace().setCurrentTool(null);

            } else if (keyEvent.getCode().equals(KeyCode.DELETE)) {
                // ---------- DEL ----------

                // Drop the current selected layers
                getCurrentWorkspace().getCurrentLayers().forEach(n->getCurrentWorkspace().removeLayer(n));

            } else if (Constants.CTRL_Z.match(keyEvent)) {
                // ---------- CTRL + Z ----------
                getCurrentWorkspace().getHistory().undo();
            } else if (Constants.CTRL_Y.match(keyEvent)) {
                // ---------- CTRL + Y ----------
                getCurrentWorkspace().getHistory().redo();
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
                         * Passing by BoundsInParent coordinate system gives us the right coordinates.
                         */
                        double posXWCoord = selection.getRectangle().getBoundsInParent().getMinX();
                        double posYWCoord = selection.getRectangle().getBoundsInParent().getMinY();
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
            cc.putString(Utils.serializeNodeList(nodes));
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
            return Utils.deserializeNodeList(serializedObject);
        } catch (Exception e) {
            // TODO: manage exceptions
            e.printStackTrace();
            return null;
        }
    }
    
    private void displayToolSetting(Button button, HBox toolBox) {
       toolSettingsContainer.getChildren().clear();
       if (toolBox != null)
         toolSettingsContainer.getChildren().add(toolBox);
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
        
        toolButtons.add(button);

        return button;
    }
    
    private void clearSelectedButtons() {
       for (Button b : toolButtons) {
          if (b.getStyleClass().contains("selected")) {
             b.getStyleClass().remove("selected");
          }
       }
       toolSettingsContainer.getChildren().clear();
    }
    
    private void selectButton(Button b) {
       b.getStyleClass().add("selected");
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
           if (workspaces.getSelectionModel().getSelectedItem() != welcomeTab)  {
             return (Workspace) workspaces.getSelectionModel().getSelectedItem().getContent();
           }
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
    private void createSlider(GridPane pane, String string, Slider slider, Label value, int position) {
        Label label = new Label(string);
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
     * SepiaTone as input, which itself has a GaussianBlur as input.
     * @param n Node
     * @return node's ColorAdjust
     */
    private ColorAdjust getColorAdjust(Node n){
        if(!(n.getEffect() instanceof ColorAdjust)){
            ColorAdjust c = new ColorAdjust();
            SepiaTone s = new SepiaTone(0);
            GaussianBlur g = new GaussianBlur(0);
            s.setInput(g);
            c.setInput(s);
            n.setEffect(c);
        }
        return ((ColorAdjust) n.getEffect());
    }

    /**
     * Sets GaussianBlur radius to desired amount for a given node.
     * @param n
     * @param i 
     */
    private void setBlurRadius(Node n, int i) {
        ((GaussianBlur) (((SepiaTone) getColorAdjust(n).getInput())).getInput()).setRadius(i);
    }
    
    private void setHoverHint(Button button, String text) {
        final ButtonPopupLabel popup = new ButtonPopupLabel(text);
        button.setOnMouseEntered(new EventHandler<MouseEvent>() {
          @Override
          public void handle(MouseEvent t) {
             mainAnchorPane.getChildren().add(popup);
             
             popup.setLayoutX(button.localToScene(button.getBoundsInLocal()).getMinX());
             popup.setLayoutY(button.localToScene(button.getBoundsInLocal()).getMaxY());
          }

       });
        
         button.setOnMouseExited(new EventHandler<MouseEvent>() {
          @Override
          public void handle(MouseEvent t) {
             mainAnchorPane.getChildren().remove(popup);
          }

       });
    }
}
