package ch.heigvd.workspace;

import ch.heigvd.gemms.Utils;

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
    private Stack<List<Integer>> selectedUndoHistory;
    private Stack<List<Integer>> selectedRedoHistory;

    /**
     * Contains the data to save the current states
     */
    private String currentState;
    private List<Integer> currentIndexes;

    private Workspace workspace;

    public History(Workspace workspace) {
        this.undoHistory = new Stack();
        this.redoHistory = new Stack();
        this.selectedUndoHistory = new Stack();
        this.selectedRedoHistory = new Stack();
        this.workspace = workspace;
    }

    @Override
    public void update(Observable observable, Object o) {
        save();
    }

    /**
     * Save the current states to the stacks
     */
    private void save() {
        System.out.println("Save");
        // If a modification is done, a new "branch" begins. No action to redo anymore
        redoHistory.clear();
        selectedRedoHistory.clear();

        try {
            // Get the selected layers indexes
            List<Integer> indexes = new LinkedList<>();
            workspace.getCurrentLayers().forEach(n -> indexes.add(workspace.getLayers().indexOf(n)));

            // Push the current states except the first time an action is done
            if (currentState != null) {
                selectedUndoHistory.push(currentIndexes);
                undoHistory.push(currentState);
            }

            // Save the current states
            currentState = Utils.serializeNodeList(workspace.getLayers());
            currentIndexes = indexes;
        } catch (Exception e) {
            // TODO: Manage exceptions
            e.printStackTrace();
        }
    }

    /**
     * Undo the last action (rollback to layers precedent state)
     */
    public void undo() {
        historyAction(undoHistory, redoHistory, selectedUndoHistory, selectedRedoHistory);
    }

    /**
     * Redo the last canceled action
     */
    public void redo() {
        historyAction(redoHistory, undoHistory, selectedRedoHistory, selectedUndoHistory);
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
            System.out.println("Action");

            // Save current state
            layersPutIn.push(currentState);
            indexesPutIn.push(currentIndexes);

            try {
                workspace.getLayers().clear();
                // Selected layers
                workspace.getCurrentLayers().clear();

                currentState = layersTakeFrom.pop();
                currentIndexes = indexesTakeFrom.pop();

                workspace.getLayers().addAll(Utils.deserializeNodeList(currentState));
                currentIndexes.forEach(i -> workspace.selectLayerByIndex(i));

            } catch (Exception e) {
                // TODO: Manage exceptions
                e.printStackTrace();
            }


        }
    }
}
