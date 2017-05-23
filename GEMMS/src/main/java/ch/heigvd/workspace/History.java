package ch.heigvd.workspace;

import ch.heigvd.gemms.Utils;
import ch.heigvd.layer.IGEMMSNode;
import javafx.scene.Node;

import java.util.*;

/**
 * Created by lognaume on 5/22/17.
 */
public class History implements Observer {
    private Stack<String> undoHistory;
    private Stack<String> redoHistory;
    private Stack<List<Integer>> selectedLayerHistory;

    private boolean undoChain = false;

    private Workspace workspace;

    public History(Workspace workspace) {
        this.undoHistory = new Stack();
        this.redoHistory = new Stack();
        this.selectedLayerHistory = new Stack();
        this.workspace = workspace;
        save();
    }

    @Override
    public void update(Observable observable, Object o) {
        System.out.println("Update");
        save();
    }

    private void save() {
        undoChain = false;
        redoHistory.clear();
        List indexes = new LinkedList();

        System.out.println("Saving " + workspace.getLayers().size() + " layers");
        try {
            undoHistory.push(Utils.serializeNodeList(workspace.getLayers()));
        } catch (Exception e) {
            // TODO: Manage exceptions
            e.printStackTrace();
        }

        workspace.getCurrentLayers().forEach(n -> {
            int index = workspace.getLayers().indexOf(n);
            if (index >= 0) {
                System.out.println("Selected : " + index);
                indexes.add(index);
            }
        });

        selectedLayerHistory.push(indexes);
    }

    public void undo() {
        if (!undoHistory.isEmpty()) {
            System.out.println("Undo");

            if (!undoChain) {
                redoHistory.push(undoHistory.pop());
            }

            undoChain = true;

            try {
                System.out.println("Clear, "+workspace.getLayers().size());
                workspace.getLayers().clear();

                String poppedElement = undoHistory.pop();

                workspace.getLayers().addAll(Utils.deserializeNodeList(poppedElement));
                redoHistory.push(poppedElement);
            } catch (Exception e) {
                // TODO: Manage exceptions
                e.printStackTrace();
            }

            // Selected layers
            workspace.getCurrentLayers().clear();

            // List<Integer> indexes = selectedLayerHistory.pop();
            // indexes.forEach(i -> workspace.getCurrentLayers().add(workspace.getLayers().get(i)));
        }
    }
}
