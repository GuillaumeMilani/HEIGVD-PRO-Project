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
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.control.Dialog;
import javafx.scene.control.Tooltip;
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
    private GridPane gridFilterTools;

    
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
    
    // List of created tool buttons
    private LinkedList<Button> toolButtons = new LinkedList();
    
    private StackPane welcomeTab;
    
    
    
   final ToolColorSettings textColor = new ToolColorSettings(ColorSet.getInstance().getColor());
   final ToolFontSettings textFont = new ToolFontSettings(6, 300, GEMMSText.DEFAULT_SIZE);
    
   final ToolSizeSettings brushSizer = new ToolSizeSettings(1, 150, 5);
   final ToolSettingsContainer brushSettings = new ToolSettingsContainer(brushSizer);
   
   final ToolSizeSettings eraserSizer = new ToolSizeSettings(1, 150, 5);
   final ToolSettingsContainer eraserSettings = new ToolSettingsContainer(eraserSizer);
   
   final ToolSettingsContainer textSettings = new ToolSettingsContainer(textColor, textFont);
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Document list
        documents = new ArrayList<>();
        

        // Create a welcome panel
        welcomeTab = new StackPane();
        welcomeTab.setAlignment(Pos.CENTER);
        
        // Create a VBox to contain elements
        VBox welcomeContainer = new VBox();
        welcomeContainer.setPrefWidth(600);
        welcomeContainer.setBackground(new Background(new BackgroundFill(Color.web("#cdcdcd"), CornerRadii.EMPTY, Insets.EMPTY)));
        welcomeContainer.setPadding(new Insets(20));
        welcomeContainer.setAlignment(Pos.CENTER);
        welcomeContainer.setSpacing(30);
        
        // Grid for multiple panels
        GridPane welcomeGrid = new GridPane();
        
        // Button for new document invite
        Button newButtonInvite = new Button();
        newButtonInvite.getStyleClass().add("new-document-button");
        newButtonInvite.setOnAction(e -> {
           newButtonAction(e);
        });
        WelcomeInvite newInvite = new WelcomeInvite(new Label("Create a new document."), newButtonInvite);
        
        // Button for open document invite
        Button openButtonInvite = new Button();
        openButtonInvite.getStyleClass().add("open-document-button");
        openButtonInvite.setOnAction(e -> {
           openButtonAction(e);
        });
        WelcomeInvite openInvite = new WelcomeInvite(new Label("open a GEMMS document."), openButtonInvite);
        
        // Add invites
        welcomeGrid.add(newInvite, 0, 0);
        welcomeGrid.add(openInvite, 1, 0);
        
        // Set visual parameters
        welcomeGrid.setHgap(15); 
        welcomeGrid.setVgap(30);
        welcomeGrid.setMaxSize(460, 460);
        
        // Add grid to the container
        welcomeContainer.getChildren().add(welcomeGrid);
        welcomeTab.getChildren().add(welcomeContainer);
        
        showWelcome();
        
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
        blur.setOnMouseReleased(eh);
        
        // Container for effect buttons and sliders
        GridPane effectsContainer = new GridPane();
        effectsContainer.setPadding(new Insets(15));
        effectsContainer.setBackground(new Background(new BackgroundFill(Color.web("#ededed"), CornerRadii.EMPTY, Insets.EMPTY)));
        effectsContainer.setStyle("-fx-effect: dropshadow( gaussian , rgba(0,0,0,0.15) , 3 ,0 , 3 , 3 );");
        effectsContainer.setHgap(5);
        effectsContainer.setVgap(10);
        effectsContainer.setLayoutX(-10000);
        effectsContainer.setLayoutY(0);
        // Add it to the mainAnchorPane
        mainAnchorPane.getChildren().add(effectsContainer);
        
        // GridPane to contain the effects buttons, not the sliders
        GridPane effectButtonsContainer = new GridPane();
        effectButtonsContainer.setPrefWidth(153);
        effectButtonsContainer.setMaxWidth(153);
        effectButtonsContainer.setHgap(10);
        // Add column constraints
        for (int i = 0; i < 3; ++i) {
            ColumnConstraints c = new ColumnConstraints();
            c.setPercentWidth(100/3.0);
            c.setHgrow(Priority.SOMETIMES);
            c.setMinWidth(10);
            c.setMaxWidth(100);
            effectButtonsContainer.getColumnConstraints().add(c);
        }

        // Create filter button
        Button BW = createToolButton("B&W", effectButtonsContainer);
        BW.setTooltip(new Tooltip("Apply a black & white filter"));
        BW.setOnAction((ActionEvent e) -> {
            Workspace w = getCurrentWorkspace();
            if (w != null) {
               clearSelectedButtons();
                for (Node n : w.getCurrentLayers()) {
                    getColorAdjust(n).setSaturation(-1);
                    saturation.setValue(-1);
                }
                w.notifyHistory();

                displayToolSetting(null);
            }
        });
        
        // Create filter button
        Button tint = createToolButton("Tint", effectButtonsContainer);
        tint.setTooltip(new Tooltip("Apply a color filter of the current color"));
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

                displayToolSetting(null);
            }
        });

        
        // Create filter button
        Button reset = createToolButton("Reset", effectButtonsContainer);
        reset.setTooltip(new Tooltip("Reset all color effects"));
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
                
                displayToolSetting(null);
            }
        });
        
        // Add the sliders to the effects container
       createSlider(effectsContainer, "Opacity:", opacity, opacityValue, 1);
       createSlider(effectsContainer, "Sepia:", sepia, sepiaValue, 2);
       createSlider(effectsContainer, "Saturation:", saturation, saturationValue, 3);
       createSlider(effectsContainer, "Contrast:", contrast, contrastValue, 4);
       createSlider(effectsContainer, "Brightness:", brightness, brightnessValue, 5);
       createSlider(effectsContainer, "Blur", blur, blurValue, 6);
       
       // Add the buttons on the first row
       effectsContainer.add(effectButtonsContainer, 0, 0);
       GridPane.setColumnSpan(effectButtonsContainer, 3);
       
       // Create a button to toggle the effect panel
       Button effectsToggl = createToolButton("Effects", gridFilterTools);
       effectsToggl.setTooltip(new Tooltip("Open/Close effects panel"));
       effectsToggl.setPrefWidth(160);
       toolButtons.remove(effectsToggl);
       // Set the toggle action
       effectsToggl.setOnAction((ActionEvent e) -> {
          // If the layoutX property >= 0, then we assume the container is visible
          if (effectsContainer.getLayoutX() >= 0) {
             //Hide the container
             effectsToggl.getStyleClass().remove("selected");
             effectsContainer.setLayoutX(-10000);
             effectsContainer.setLayoutY(0);
          } else { // The container is not visible
             selectButton(effectsToggl);

             // Get height of the window
             double windowHeight = mainAnchorPane.getBoundsInParent().getHeight();

             // Get the height of the container
             double containerHeight = effectsContainer.getBoundsInParent().getHeight();

             // Get the ideal position of the panel
             double posX = effectsToggl.localToScene(effectsToggl.getBoundsInLocal()).getMinX();
             double posY = effectsToggl.localToScene(effectsToggl.getBoundsInLocal()).getMaxY();

             // If the container would overflow from the window
             if (posY + containerHeight > windowHeight) {
                posX = 180;
                posY = windowHeight - containerHeight;
             }
             
             // Set the container position
             effectsContainer.setLayoutX(posX);
             effectsContainer.setLayoutY(posY);
          }
       });

        mainAnchorPane.setOnKeyPressed(keyEvent -> {
            Workspace w = getCurrentWorkspace();
            // ---------- ESC ----------
            if (keyEvent.getCode().equals(KeyCode.ESCAPE)) {
                // Disable current tool
                if (w != null) {
                   w.setCurrentTool(null);
                  clearSelectedButtons();
                  w.getLayerTool().setCursor(javafx.scene.Cursor.DEFAULT);
                }
                

            } else if (keyEvent.getCode().equals(KeyCode.DELETE)) {
                // ---------- DEL ----------

                if (w != null && w.getLayerTool() != null && w.getCurrentTool() instanceof Selection) { 
                   
                   
                    // Get the selection
                    Selection selection = (Selection)getCurrentWorkspace().getCurrentTool();
                    Rectangle rec = selection.getRectangle();

                    for (Node n : w.getCurrentLayers()) {
                       if(n instanceof GEMMSCanvas) {

                          try {
                             GEMMSCanvas canvas = (GEMMSCanvas)n;
                             
                             GraphicsContext gc = canvas.getGraphicsContext2D();
                             gc.setTransform(new Affine(canvas.localToParentTransformProperty().get().createInverse()));
                             gc.clearRect(rec.getBoundsInParent().getMinX(), rec.getBoundsInParent().getMinY(), rec.getWidth(), rec.getHeight());
                          } catch (NonInvertibleTransformException ex) {
                             
                             // TODO : Manage exceptions
                             Logger.getLogger(GEMMSStageFXMLController.class.getName()).log(Level.SEVERE, null, ex);
                          }
                       }
                    }
                }
                else {
                  // Drop the current selected layers
                  w.getCurrentLayers().forEach(n->w.removeLayer(n));
                }

            } else if (w != null && Constants.CTRL_Z.match(keyEvent)) {
                // ---------- CTRL + Z ----------
                
                getCurrentWorkspace().getHistory().undo();
            } else if (w != null && Constants.CTRL_Y.match(keyEvent)) {
                // ---------- CTRL + Y ----------
                w.getHistory().redo();
            }

            // ---------- CTRL + C ----------
            if (Constants.CTRL_C.match(keyEvent)) {

                // In case there is a selection
                if (w != null && w.getLayerTool() != null && w.getCurrentTool() instanceof Selection) {

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
                    for (Node n : w.getCurrentLayers()) {
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
                } else if (w!= null && w.getCurrentLayers() != null) {
                    saveNodesToClipboard(w.getCurrentLayers());
                }

            } else if (Constants.CTRL_V.match(keyEvent)) {
                for (Node n : getNodesFromClipboard()) {
                    w.addLayer(n);
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
    
    private void displayToolSetting(HBox toolBox) {
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
    
    

   /**
    * Action when clicked on new canvas button.
    * Create a canvas and add it to the current workspace's layer list
    * 
    * @param e 
    */
   @FXML private void newCanvasButtonAction(ActionEvent e) {
      clearSelectedButtons();
      Workspace w = getCurrentWorkspace();
       if(w != null) {
           w.addLayer(new GEMMSCanvas(w.width(), w.height()));
           displayToolSetting(null);
       }
   }
   
   
   /**
    * Action when clicked on new image button.
    * Import an image and add it to the current workspace's layer list
    * 
    * @param e 
    */
   @FXML private void newImageButtonAction(ActionEvent e) {
      Workspace w = getCurrentWorkspace();
       if(w != null) {
           ImportImageDialog dialog = new ImportImageDialog(stage);
           Image image = dialog.showAndWait();
           if(image != null) {
               GEMMSImage i = new GEMMSImage(image);
               i.setViewport(new Rectangle2D(0, 0, image.getWidth(), image.getHeight()));
               w.addLayer(i);
               displayToolSetting(null);
           }
       }
   }

   
   /**
    * Action when clicked on new text button.
    * Create a text and add it to the current workspace's layer list
    * 
    * @param e 
    */
   @FXML private void newTextButtonAction(ActionEvent e) {
     clearSelectedButtons();
     Workspace w = getCurrentWorkspace();
     if(w != null) {
        Optional<String> result = TextTool.getDialogText(null);
        if (result.isPresent()) {
           GEMMSText t = new GEMMSText(w.width()/2, w.height()/2, result.get());
           t.setFill(textColor.getColor());
           t.setFont(textFont.getFont());
           t.setTranslateX(-t.getBoundsInParent().getWidth() / 2);
           w.addLayer(t);
           displayToolSetting(null);
        }
        clearSelectedButtons();
     }
   }
   
   
   /**
    * Action when clicked on brush button.
    * Set the current tool with a brush tool
    * 
    * @param e 
    */
   @FXML private void brushButtonAction(ActionEvent e) {
      Button source = (Button)e.getSource();
      toolButtons.add(source);
      
      Workspace w = getCurrentWorkspace();
      if(w != null) {
         clearSelectedButtons();
         selectButton(source);
         Brush b = new Brush(w);
         w.setCurrentTool(b);
         brushSizer.setTarget(b);
         displayToolSetting(brushSettings);
      }
   }

   
   /**
    * Action when clicked on eraser button.
    * Set the current tool with an eraser tool
    * 
    * @param e 
    */
   @FXML private void eraserButtonAction(ActionEvent e) {
      Button source = (Button)e.getSource();
      toolButtons.add(source);
      
      Workspace w = getCurrentWorkspace();
      if(w != null) {
         clearSelectedButtons();
         selectButton(source);
         Eraser er = new Eraser(w);
         w.setCurrentTool(er);
         eraserSizer.setTarget(er);
         displayToolSetting(eraserSettings);
      }
   }
   
   
   /**
    * Action when clicked on eye dropper button.
    * Set the current tool with an eye dropper tool
    * 
    * @param e 
    */
   @FXML private void eyeDropperButtonAction(ActionEvent e) {
      Button source = (Button)e.getSource();
      toolButtons.add(source);
      
      Workspace w = getCurrentWorkspace();
      if(w != null) {
         clearSelectedButtons();
         selectButton(source);
         w.setCurrentTool(new EyeDropper(w));
        displayToolSetting(null);
      }
   }
   
   
   /**
    * Action when clicked on text button.
    * Set the current tool with an edit text tool
    * 
    * @param e 
    */
   @FXML private void textButtonAction(ActionEvent e) {
      Button source = (Button)e.getSource();
      toolButtons.add(source);
      
      clearSelectedButtons();
       Workspace w = getCurrentWorkspace();
       if(w != null) {
          selectButton(source);
          TextTool t = new TextTool(w);
          w.setCurrentTool(t); 
          textColor.setTarget(t);
          textFont.setTarget(t);
          displayToolSetting(textSettings);
       }
   }
   
   
   /**
    * Action when clicked on horizontal symmetry button.
    * Tranform all selected layers with horizontal symmetry
    * 
    * @param e 
    */
   @FXML private void hSymmetryButtonAction(ActionEvent e) {
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
        displayToolSetting(null);
      }
   }
   
   
   /**
    * Action when clicked on vertical symmetry button.
    * Tranform all selected layers with vertical symmetry
    * 
    * @param e 
    */
   @FXML private void vSymmetryButtonAction(ActionEvent e) {
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
         displayToolSetting(null);
      }
   }
   
   
   /**
    * Action when clicked on drag button.
    * Set the current tool with a drag tool
    * 
    * @param e 
    */
   @FXML private void dragButtonAction(ActionEvent e) {
      Button source = (Button)e.getSource();
      toolButtons.add(source);
      
      Workspace w = getCurrentWorkspace();
      if(w != null) {
         clearSelectedButtons();
         selectButton(source);
         Drag dragTool = new Drag(w);
          w.setCurrentTool(dragTool);

          //add setting alignement lines for drag
          HBox hBox = new HBox();
          Button activeAlignement = new Button("Alignement: Off");
          activeAlignement.setOnAction(new EventHandler<ActionEvent>() {
              @Override
              public void handle(ActionEvent event) {
                  dragTool.turnAlignementOnOff();
                  activeAlignement.setText("Alignement: "+ (dragTool.isAlignementActive() ? "On" : "Off"));
              }
          });
          hBox.getChildren().addAll(activeAlignement);

        displayToolSetting(hBox);
      }
   }
   
   
   /**
    * Action when clicked on rotate button.
    * Set the current tool with a rotate tool
    * 
    * @param e 
    */
   @FXML private void rotateButtonAction(ActionEvent e) {
      Button source = (Button)e.getSource();
      toolButtons.add(source);
      
      Workspace w = getCurrentWorkspace();
      if(w != null) {
         clearSelectedButtons();
         selectButton(source);
         w.setCurrentTool(new ch.heigvd.tool.Rotate(w));
         displayToolSetting(null);
      }
   }
   
   
   /**
    * Action when clicked on scale button.
    * Set the current tool with a scale tool
    * 
    * @param e 
    */
   @FXML private void scaleButtonAction(ActionEvent e) {
      Button source = (Button)e.getSource();
      toolButtons.add(source);
      
      Workspace w = getCurrentWorkspace();
      if(w != null) {
         clearSelectedButtons();
         selectButton(source);
          w.setCurrentTool(new ch.heigvd.tool.Resize(w));
        displayToolSetting(null);
      }
   }
   
   
   /**
    * Action when clicked on selection button.
    * Set the current tool with a selection tool
    * 
    * @param e 
    */
   @FXML private void selectionButtonAction(ActionEvent e) {
      Button source = (Button)e.getSource();
      toolButtons.add(source);
      
      Workspace w = getCurrentWorkspace();
      if(w != null) {
         clearSelectedButtons();
         selectButton(source);
          w.setCurrentTool(new Selection(w));
        displayToolSetting(null);
      }
   }
   
   
   /**
    * Action when clicked on crop button.
    * Set the current tool with a crop tool
    * 
    * @param e 
    */
   @FXML private void cropButtonAction(ActionEvent e) {
      Button source = (Button)e.getSource();
      toolButtons.add(source);
      
      Workspace w = getCurrentWorkspace();
      if(w != null) {
         clearSelectedButtons();
         selectButton(source);
          w.setCurrentTool(new Crop(w));
        displayToolSetting(null);
      }
   }
   
   


   
    
    @FXML
    private void newButtonAction(ActionEvent e) {
       
        hideWelcome();
        
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
       
       hideWelcome();
       
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
     * Show and set anchors for the welcome panel.
     */
    private void showWelcome() {
       mainAnchorPane.getChildren().add(welcomeTab);
       AnchorPane.setTopAnchor(welcomeTab, 40.0);
       AnchorPane.setRightAnchor(welcomeTab, 165.0);
       AnchorPane.setBottomAnchor(welcomeTab, 0.0);
       AnchorPane.setLeftAnchor(welcomeTab, 177.0);
    }
    
    /**
     * Hide the welcome panel.
     */
    private void hideWelcome() {
       mainAnchorPane.getChildren().remove(welcomeTab);
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
}
