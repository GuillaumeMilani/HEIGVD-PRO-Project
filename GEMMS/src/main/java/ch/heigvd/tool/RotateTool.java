package ch.heigvd.tool;

import ch.heigvd.layer.IGEMMSNode;
import ch.heigvd.workspace.Workspace;
import javafx.scene.Cursor;
import javafx.scene.Node;

import java.util.List;

/**
 * Created by Michael on 17.05.2017.
 */
public class RotateTool implements Tool{
    private double mouseX;
    private Workspace workspace;

    List<Node> layers;

    public RotateTool(Workspace w){
        this.workspace = w;
    }

    @Override
    public void mousePressed(double x, double y) {
        mouseX = x;
        layers = workspace.getCurrentLayers();
        workspace.setCursor(Cursor.E_RESIZE);

    }

    @Override
    public void mouseDragged(double x, double y) {

        double newX = x - mouseX;

        for (Node node : layers) {
            node.setRotationAxis(javafx.scene.transform.Rotate.Z_AXIS);
            node.setRotate(node.getRotate()-newX);
        }

        mouseX = x;

    }

    @Override
    public void mouseReleased(double x, double y) {
        workspace.setCursor(Cursor.DEFAULT);
    }

}
