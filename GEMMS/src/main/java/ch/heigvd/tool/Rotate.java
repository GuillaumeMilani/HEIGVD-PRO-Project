package ch.heigvd.tool;

import ch.heigvd.layer.IGEMMSNode;
import ch.heigvd.workspace.Workspace;
import javafx.scene.Cursor;
import javafx.scene.Node;

import java.util.List;

/**
 * Created by Michael on 17.05.2017.
 */
public class Rotate implements Tool{
    private double mouseX;
    private Workspace workspace;

    List<Node> layers;

    public Rotate(Workspace w){
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
//            centerX = node.getBoundsInParent().getWidth()/2;
//            centerY = node.getBoundsInParent().getHeight()/2;
//            r.setPivotX(centerX);
//            r.setPivotY(centerY);
//            r.setAngle(node.getRotate()-newX);
            node.setRotate(node.getRotate()-newX);
        }

        mouseX = x;

    }

    @Override
    public void mouseReleased(double x, double y) {
        workspace.setCursor(Cursor.DEFAULT);
    }

}
