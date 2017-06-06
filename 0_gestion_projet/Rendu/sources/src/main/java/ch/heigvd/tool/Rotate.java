/**
 * Fichier: Rotate.java
 * Date: 31.05.2017
 *
 * @author Guillaume Milani
 * @author Edward Ransome
 * @author Mathieu Monteverde
 * @author Michael Spierer
 * @author Sathiya Kirushnapillai
 */
package ch.heigvd.tool;

import ch.heigvd.workspace.Workspace;
import java.util.List;
import javafx.scene.Cursor;
import javafx.scene.Node;

public class Rotate extends AbstractTool {

    //The old x coordonate
    private double mouseX;
    //The list of selected Nodes
    private List<Node> layers;

    /**
     * Constructor of Rotate Tool
     *
     * @param w workspace to crop
     */
    public Rotate(Workspace w) {
        super(w);
        workspace.getLayerTool().setCursor(Cursor.DEFAULT);
    }

    @Override
    public void mousePressed(double x, double y) {
        mouseX = x;
        layers = workspace.getCurrentLayers();
        workspace.getLayerTool().setCursor(Cursor.E_RESIZE);

    }

    @Override
    public void mouseDragged(double x, double y) {

        double newX = x - mouseX;

        for (Node node : layers) {
            node.setRotationAxis(javafx.scene.transform.Rotate.Z_AXIS);
            node.setRotate(node.getRotate() - newX);
        }
        mouseX = x;
    }

    @Override
    public void mouseReleased(double x, double y) {
        notifier.notifyHistory();
        workspace.getLayerTool().setCursor(Cursor.DEFAULT);
    }

    @Override
    public void mouseMoved(double x, double y) {
    }

}
