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
    private Stack<String> undoHistory;
    private Stack<String> redoHistory;
    private String currentState;

    private boolean actionChain = false;

    private Workspace workspace;

    public History(Workspace workspace) {
        this.undoHistory = new Stack();
        this.redoHistory = new Stack();
        this.workspace = workspace;
    }

    @Override
    public void update(Observable observable, Object o) {
        save();
    }

    private void save() {
        actionChain = false;
        redoHistory.clear();

        System.out.println("Saving " + workspace.getLayers().size() + " layers");
        try {
            undoHistory.push(currentState);
            currentState = Utils.serializeNodeList(workspace.getLayers());
        } catch (Exception e) {
            // TODO: Manage exceptions
            e.printStackTrace();
        }
    }

    public void undo() {
        historyAction(undoHistory, redoHistory);
    }

    public void redo() {
        historyAction(redoHistory, undoHistory);
    }

    private void historyAction(Stack<String> takeFrom, Stack<String> putIn) {
        if (!takeFrom.isEmpty()) {

            // Save current state
            putIn.push(currentState);

            actionChain = true;

            try {
                workspace.getLayers().clear();

                String poppedElement = takeFrom.pop();

                workspace.getLayers().addAll(Utils.deserializeNodeList(poppedElement));

                currentState = poppedElement;
            } catch (Exception e) {
                // TODO: Manage exceptions
                e.printStackTrace();
            }

            // Selected layers
            workspace.getCurrentLayers().clear();
        }
    }
}
