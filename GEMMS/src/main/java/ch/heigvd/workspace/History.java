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

    private ObservableList undoImages;
    private Stack<WritableImage> redoImages;


    /**
     * Contains the data to save the current states
     */
    private String currentState;
    private List<Integer> currentIndexes;
    private Image currentImage;

    ObservableList<Image> imagesHistory;

    private Workspace workspace;

    public History(Workspace workspace) {
        this.undoHistory = new Stack();
        this.redoHistory = new Stack();
        this.undoSelectedLayers = new Stack();
        this.redoSelectedLayers = new Stack();
        this.undoHistory = new Stack();
        this.redoHistory = new Stack();
        // this.undoImages =

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
            redoHistory.clear();
            redoSelectedLayers.clear();
            imagesHistory.remove(0, workspace.getHistoryList().getSelectionModel().getSelectedIndex());

            try {
                // Get the selected layers indexes
                List<Integer> indexes = new LinkedList<>();
                workspace.getCurrentLayers().forEach(n -> indexes.add(workspace.getLayers().indexOf(n)));

                // Push the current states except the first time an action is done
                if (currentState != null) {
                    undoSelectedLayers.push(currentIndexes);
                    undoHistory.push(currentState);
                    imagesHistory.add(workspace.getHistoryList().getSelectionModel().getSelectedIndex(), currentImage);
                }

                // Save the current states
                currentState = Utils.serializeNodeList(workspace.getLayers());
                currentIndexes = indexes;

                // Snapshot
                final SnapshotParameters sp = new SnapshotParameters();

                double scale = 120./workspace.width();
                sp.setTransform(Transform.scale(scale, scale));

                currentImage = workspace.snapshot(sp, null);
                PixelReader reader = currentImage.getPixelReader();
                WritableImage newImage = new WritableImage(reader, 0, 0, 120, (int)(workspace.height() * scale));
                currentImage = newImage;

                System.out.println(currentImage.getWidth() + " x " + currentImage.getHeight());
            } catch (Exception e) {
                // TODO: Manage exceptions
                e.printStackTrace();
            }
        });
    }

    /**
     * Undo the last action (rollback to layers precedent state)
     */
    public void undo(int number) {
        historyAction(undoHistory, redoHistory, undoSelectedLayers, redoSelectedLayers);
    }

    /**
     * Redo the last canceled action
     */
    public void redo(int number) {
        historyAction(redoHistory, undoHistory, redoSelectedLayers, undoSelectedLayers);
    }

    /**
     * Restore the history from one stack (and save the current state in the other stack)
     * @param layersTakeFrom
     * @param layersPutIn
     * @param indexesTakeFrom
     * @param indexesPutIn
     */
    private void historyAction(Stack<String> layersTakeFrom, Stack<String> layersPutIn,
                               Stack<List<Integer>> indexesTakeFrom, Stack<List<Integer>> indexesPutIn) {
        if (!layersTakeFrom.isEmpty()) {

            // Save current state
            layersPutIn.push(currentState);
            indexesPutIn.push(currentIndexes);
            imagesHistory.add(workspace.getHistoryList().getSelectionModel().getSelectedIndex(), currentImage);
            workspace.getHistoryList().getSelectionModel().select(workspace.getHistoryList().getSelectionModel().getSelectedIndex()+1);

            try {
                workspace.getLayers().clear();
                // Selected layers
                workspace.getCurrentLayers().clear();

                currentState = layersTakeFrom.pop();
                currentIndexes = indexesTakeFrom.pop();
                currentImage = imagesHistory.get(workspace.getHistoryList().getSelectionModel().getSelectedIndex());


                workspace.getLayers().addAll(Utils.deserializeNodeList(currentState));
                currentIndexes.forEach(i -> workspace.selectLayerByIndex(i));

            } catch (IOException | ClassNotFoundException e) {
                // TODO: Manage exceptions
                e.printStackTrace();
            }
        }
    }
}
