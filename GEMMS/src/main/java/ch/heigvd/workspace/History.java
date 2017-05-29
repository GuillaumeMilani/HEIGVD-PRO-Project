package ch.heigvd.workspace;

import ch.heigvd.gemms.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.application.Platform;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import java.io.IOException;
import javafx.scene.image.WritableImage;
import javafx.scene.transform.Transform;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Guillaume Milani
 * @date 23 May 2017
 * @brief Manage history for a GEMMS workspace
 *
 * Each time one of this class instance is notified it saves the workspace layer's state.
 * It is then possible to undo() (restore previous state) or redo() (restore state before undo())
 */
public class History implements Observer {
    /**
     * Stacks to save the serialized (& compressed) layers states
     */
    private Stack<String> undoHistory;
    private Stack<String> redoHistory;

    /**
     * Stacks to save the currently selected layers
     */
    private Stack<List<Integer>> undoSelectedLayers;
    private Stack<List<Integer>> redoSelectedLayers;


    /**
     * Contains the data to save the current states
     */
    private String currentState;
    private List<Integer> currentIndexes;

    List<String> history;
    List<List<Integer>> selectedHistory;
    ObservableList<Image> imagesHistory;

    private int currentIndex;

    private Workspace workspace;

    public History(Workspace workspace) {
        this.undoHistory = new Stack();
        this.redoHistory = new Stack();
        this.undoSelectedLayers = new Stack();
        this.redoSelectedLayers = new Stack();
        this.undoHistory = new Stack();
        this.redoHistory = new Stack();
        currentIndex = 0;

        this.history = new LinkedList<>();
        this.selectedHistory = new LinkedList<>();
        this.workspace = workspace;
        this.imagesHistory = workspace.getHistoryList().getItems();

        workspace.getHistoryList().setCellFactory(listView -> new ListCell<Image>() {
            private ImageView imageView = new ImageView();
            @Override
            public void updateItem(Image image, boolean empty) {
                super.updateItem(image, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    imageView.setFitHeight(image.getHeight());
                    imageView.setFitWidth(image.getWidth());
                    System.out.println(image.getHeight());
                    imageView.setImage(image);
                    setGraphic(imageView);
                }
            }
        });
    }

    @Override
    public void update(Observable observable, Object o) {
        save();
    }

    /**
     * Save the current states to the stacks
     */
    private void save() {
        Platform.runLater(() -> {
            // If a modification is done, a new "branch" begins. No action to redo anymore
            history.subList(0, currentIndex).clear();
            selectedHistory.subList(0, currentIndex).clear();
            imagesHistory.remove(0, currentIndex);

            currentIndex = 0;

            try {
                // Get the selected layers indexes
                List<Integer> indexes = new LinkedList<>();
                workspace.getCurrentLayers().forEach(n -> indexes.add(workspace.getLayers().indexOf(n)));

                // Get the thumbnail for visual history
                final SnapshotParameters sp = new SnapshotParameters();

                double scale = 120./workspace.width();
                sp.setTransform(Transform.scale(scale, scale));

                Image snapshot = workspace.snapshot(sp, null);
                PixelReader reader = snapshot.getPixelReader();
                WritableImage newImage = new WritableImage(reader, 0, 0, 120, (int)(workspace.height() * scale));

                // Save the current states
                history.add(0,Utils.serializeNodeList(workspace.getLayers()));
                selectedHistory.add(0, indexes);
                imagesHistory.add(0, newImage);

                workspace.getHistoryList().getSelectionModel().select(currentIndex);
            } catch (Exception e) {
                Logger.getLogger(History.class.getName()).log(Level.SEVERE, null, e);
            }
        });
    }

    public void undo() {
        restoreToIndex(currentIndex + 1);
    }

    /**
     * Redo the last canceled action
     */
    public void redo() {
        restoreToIndex(currentIndex - 1);
    }

    /**
     * Restore the workspace at the state in parameter
     */
    public void restoreToIndex(int index) {
        if (index < 0 || index > history.size()) {
            Logger.getLogger(History.class.getName()).log(Level.SEVERE, "Trying to restore a state at index out of bounds");
        } else {
            // Save current state
            currentIndex = index;
            workspace.getHistoryList().getSelectionModel().select(currentIndex);
            System.out.println(currentIndex);
            try {
                workspace.getLayers().clear();
                // Selected layers
                workspace.getCurrentLayers().clear();

                workspace.getLayers().addAll(Utils.deserializeNodeList(history.get(currentIndex)));
                selectedHistory.get(currentIndex).forEach(i -> workspace.selectLayerByIndex(i));

            } catch (IOException | ClassNotFoundException e) {
                Logger.getLogger(History.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }
}
