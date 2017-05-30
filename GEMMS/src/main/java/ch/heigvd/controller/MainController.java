package ch.heigvd.controller;


import ch.heigvd.gemms.Constants;
import ch.heigvd.gemms.Document;
import ch.heigvd.gemms.Utils;
import ch.heigvd.gemms.WelcomeInvite;
import ch.heigvd.layer.GEMMSCanvas;
import ch.heigvd.tool.*;
import ch.heigvd.workspace.Workspace;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.*;
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
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.layout.StackPane;



public class MainController implements Initializable {
   
   
   @FXML
   private ToolbarController toolbarController;
   
   @FXML
   private ToolboxController toolboxController;

    // Stage from main
    private Stage stage;

    @FXML
    private AnchorPane mainAnchorPane;

    /**
     * GridPanes containing the tools buttons
     */



    
    // Contains all workspace (tab)
    @FXML
    private TabPane workspaces;
    
    
    @FXML
    private VBox layerController;

    @FXML
    private VBox historyViewer;
    
    @FXML
    private AnchorPane colorController;
    


    // List of documents
    private ArrayList<Document> documents;
    

    
    private StackPane welcomeTab;
    
    
    

    
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
           toolbarController.newButtonAction(e);
        });
        WelcomeInvite newInvite = new WelcomeInvite(new Label("Create a new document."), newButtonInvite);
        
        // Button for open document invite
        Button openButtonInvite = new Button();
        openButtonInvite.getStyleClass().add("open-document-button");
        openButtonInvite.setOnAction(e -> {
           toolbarController.openButtonAction(e);
        });
        WelcomeInvite openInvite = new WelcomeInvite(new Label("Open a GEMMS document."), openButtonInvite);
        
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
                historyViewer.getChildren().clear();
                historyViewer.getChildren().add(w.getHistoryList());
                w.setCurrentTool(null);
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
                historyViewer.getChildren().clear();
              }
              toolboxController.clearSelectedButtons();
        });
        
        
        
        colorController.getChildren().add(ColorSet.getInstance().getColorController());

        
        
        
        
        mainAnchorPane.setOnKeyPressed(keyEvent -> {
            Workspace w = getCurrentWorkspace();
            // ---------- ESC ----------
            if (keyEvent.getCode().equals(KeyCode.ESCAPE)) {
                // Disable current tool
                if (w != null) {
                   w.setCurrentTool(null);
                  toolboxController.clearSelectedButtons();
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
                             Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
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
            } else if (Constants.CTRL_Y.match(keyEvent)) {
                // ---------- CTRL + Y ----------
                getCurrentWorkspace().getHistory().redo();
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
        
      toolbarController.init(this);
      toolboxController.init(this, toolbarController);
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
    

    

    

    
    
   /**
    * Create a new tab
    * 
    * @param document that containt name and workspace
    */
   public void createTab(Document document) {
      Workspace w = document.workspace();

      // Create tab
      Tab tab = new Tab(document.name(), w);
      workspaces.getTabs().add(tab);
      workspaces.getSelectionModel().select(tab);
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
    public void hideWelcome() {
       mainAnchorPane.getChildren().remove(welcomeTab);
    }
    
   public void addDocument(Document document) {
      documents.add(document);
   }
    
    /**
     * Get the current workspace displayed
     * 
     * @return current workspace
     */
    public Workspace getCurrentWorkspace() {
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
    public Document getDocument(Workspace w) {
        for(Document d : documents) {
            if(d.workspace() == w) {
               return d;
            }
        }
        
        return null;
    }

   public Tab getCurrentTab() {
      return workspaces.getSelectionModel().getSelectedItem();
   }
   
   public AnchorPane getMainPane() {
      return mainAnchorPane;
   }
   
   /**
    * Set main stage
    * 
    * @param s stage to set
    */
   public void setStage(Stage s) {
      this.stage = s;
   }
   
   /**
    * Get main stage
    * 
    * @return stage
    */
   public Stage getStage() {
      return stage;
   }
}
