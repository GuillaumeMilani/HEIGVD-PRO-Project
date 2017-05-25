package ch.heigvd.tool;

import ch.heigvd.workspace.Workspace;
import javafx.scene.Cursor;
import javafx.scene.Node;

import java.util.List;

public class Drag extends AbstractTool {

    //The old coordinates
    private double lastX;
    private double lastY;
    //The list of selected Nodes
    private List<Node> layers;

    /**
     * Constructor of Drag Tool
     *
     * @param w workspace to crop
     */    public Drag(Workspace w){
        super(w);
    }

    @Override
    public void mousePressed(double x, double y) {
        workspace.setCursor(Cursor.CLOSED_HAND);

        lastX = x;
        lastY = y;
        layers = workspace.getCurrentLayers();
    }

    @Override
    public void mouseDragged(double x, double y) {

        //offsets to change coordonates
        double offsetX = x - lastX;
        double offsetY = y - lastY;

        for(Node n : layers) {
            n.setTranslateX(n.getTranslateX() + offsetX);
            n.setTranslateY(n.getTranslateY() + offsetY);
        }

        lastX = x;
        lastY = y;
    }

    @Override
    public void mouseReleased(double x, double y) {
        workspace.setCursor(Cursor.DEFAULT);
        notifier.notifyHistory();
    }
}