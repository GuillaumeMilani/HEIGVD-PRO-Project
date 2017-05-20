package ch.heigvd.tool;

import ch.heigvd.layer.GEMMSCanvas;
import ch.heigvd.workspace.Workspace;
        import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.transform.*;

import java.util.List;


public class Resize implements Tool {

    private final Workspace workspace;
    private double mouseX;
    private double mouseY;
    private final double FACTEUR = 0.001;
    Scale s = new Scale();

    public Resize(Workspace w){
        this.workspace = w;
        s = new Scale();
    }

    @Override
    public void mousePressed(double x, double y) {
        mouseX = x;
        mouseY = y;
        workspace.setCursor(Cursor.E_RESIZE);
    }

    @Override
    public void mouseDragged(double x, double y) {


        double newX = x - mouseX;
        double newY = y - mouseY;

        List<Node> layers = workspace.getCurrentLayers();

        for (Node node : layers) {
            double scale = s.getX();
            double newScale = node.getScaleX() + newX * FACTEUR;
            if(newScale>=0) {
                node.setScaleX(newScale);
                node.setScaleY(newScale);
                node.setScaleZ(newScale);
            }
        }

        mouseX = x;
        mouseY = y;
    }

    @Override
    public void mouseReleased(double x, double y) {
        workspace.setCursor(Cursor.DEFAULT);
    }
}