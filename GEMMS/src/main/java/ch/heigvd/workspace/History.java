package ch.heigvd.workspace;

import ch.heigvd.layer.IGEMMSNode;
import javafx.scene.Node;

import java.util.*;

/**
 * Created by lognaume on 5/22/17.
 */
public class History implements Observer {
    private Stack<List<Node>> history;
    private Stack<List<Integer>> selectedLayerHistory;

    private Workspace workspace;

    public History(Workspace workspace) {
        this.history = new Stack();
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
        List nodes = new LinkedList();
        List indexes = new LinkedList();

        workspace.getLayers().forEach(n -> {
            nodes.add(((IGEMMSNode)n).clone());
        });

        workspace.getCurrentLayers().forEach(n -> {
            int index = workspace.getChildren().indexOf(n);
            if (index >= 0) {
                indexes.add(index);
            }
        });

        history.push(nodes);
        selectedLayerHistory.push(indexes);
    }

    public void undo() {
        if (!history.isEmpty()) {
            System.out.println("Undo");
            workspace.setLayers(history.pop());

            // Selected layers
            workspace.getCurrentLayers().clear();

            List<Integer> indexes = selectedLayerHistory.pop();
            indexes.forEach(i -> workspace.getCurrentLayers().add(workspace.getLayers().get(i)));
        }
    }
}
