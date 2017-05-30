package ch.heigvd.controller;

import ch.heigvd.dialog.NewDocument;
import ch.heigvd.dialog.NewDocumentDialog;
import ch.heigvd.dialog.OpenDocumentDialog;
import ch.heigvd.dialog.ResizeDialog;
import ch.heigvd.gemms.Document;
import ch.heigvd.layer.GEMMSCanvas;
import ch.heigvd.workspace.Workspace;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Tab;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class ToolbarController {

   // Main controller
   private MainController mainController;

   @FXML
   private HBox toolSettingsContainer;

   // Init this controller
   public void init(MainController c) {
      mainController = c;
   }

   public void addToolSettings(HBox toolBox) {
      toolSettingsContainer.getChildren().add(toolBox);
   }

   public void clearToolSettings() {
      toolSettingsContainer.getChildren().clear();
   }

   public void displayToolSetting(HBox toolBox) {
      clearToolSettings();
      if (toolBox != null) {
         addToolSettings(toolBox);
      }
   }

   @FXML
   protected void newButtonAction(ActionEvent e) {

      mainController.hideWelcome();

      // Create a new dialog
      NewDocumentDialog dialog = new NewDocumentDialog();

      // Display dialog
      Optional<NewDocument> result = dialog.showAndWait();

      // Dialog OK
      if (result.isPresent()) {

         int width = result.get().getWidth();
         int height = result.get().getHeiht();
         Color color = result.get().getColor();

         // Create a new document
         Document document = new Document(mainController.getStage(), width, height);

         // Get workspace
         Workspace w = document.workspace();

         GEMMSCanvas canvas = new GEMMSCanvas(width, height);
         GraphicsContext gc = canvas.getGraphicsContext2D();
         gc.setFill(color);
         gc.fillRect(0, 0, width, height);

         mainController.createTab(document);

         // Set background
         w.addLayer(canvas);

         mainController.addDocument(document);
      }
   }

   @FXML
   protected void openButtonAction(ActionEvent e) {

      mainController.hideWelcome();

      OpenDocumentDialog dialog = new OpenDocumentDialog(mainController.getStage());

      File f = dialog.showAndWait();
      if (f != null) {
         Document document = null;
         try {
            document = new Document(mainController.getStage(), f);
         } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
         }

         mainController.createTab(document);

         mainController.addDocument(document);
      }
   }

   @FXML
   protected void saveButtonAction(ActionEvent e) {
      Workspace w = mainController.getCurrentWorkspace();
      if (w != null) {
         // Get current tab
         Tab tab = mainController.getCurrentTab();

         // Research document with workspace
         Document d = mainController.getDocument(w);

         // Save document
         if (d != null) {
            try {
               d.save();
            } catch (IOException ex) {
               Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            }
         }

         // Set tab title
         tab.setText(d.name());
      }
   }

   @FXML
   protected void exportButtonAction(ActionEvent e) {
      Workspace w = mainController.getCurrentWorkspace();
      if (w != null) {
         // Research document with workspace
         Document d = mainController.getDocument(w);

         // export document as image
         if (d != null) {
            try {
               d.export();
            } catch (IOException ex) {
               Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
            }
         }
      }
   }

   @FXML
   protected void resizeButtonAction(ActionEvent e) {
      Workspace w = mainController.getCurrentWorkspace();
      if (w != null) {

         ResizeDialog dialog = new ResizeDialog(w);

         Optional<Rectangle> result = dialog.showAndWait();

         if (result.isPresent()) {

            w.resizeCanvas((int) result.get().getWidth(),
                    (int) result.get().getHeight(),
                    (int) result.get().getX(),
                    (int) result.get().getY());
         }
      }
   }

}
