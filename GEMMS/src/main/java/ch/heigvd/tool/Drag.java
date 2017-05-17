package ch.heigvd.tool;

import ch.heigvd.workspace.Workspace;
import javafx.scene.Node;

import java.util.List;

/**
 * Created by Michael on 17.05.2017.
 */
public class Drag implements Tool{
    private double mouseX;
    private double mouseY;
    private Workspace workspace;

    public Drag(Workspace w){
        this.workspace = w;
    }
    @Override
    public void mousePressed(double x, double y) {
        mouseX = x;
        mouseY = y;
    }

    @Override
    public void mouseDragged(double x, double y) {
        double newX = x - mouseX;
        double newY = y - mouseY;

        List<Node> layers = workspace.getCurrentLayers();

        for (Node node : layers) {

            node.setTranslateX(node.getTranslateX() + newX);
            node.setTranslateY(node.getTranslateY()+newY);
        }

        mouseX = x;
        mouseY = y;

    }

    @Override
    public void mouseReleased(double x, double y) {

    }
}
