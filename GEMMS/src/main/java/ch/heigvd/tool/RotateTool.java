package ch.heigvd.tool;

import ch.heigvd.workspace.Workspace;
import java.util.List;
import javafx.scene.Cursor;
import javafx.scene.Node;



public class RotateTool extends AbstractTool {
    private double mouseX;

    List<Node> layers;

    public RotateTool(Workspace w){
        super(w);
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
        notifier.notifyHistory();
    }

}
