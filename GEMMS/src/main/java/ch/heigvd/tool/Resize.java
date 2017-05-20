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
    private final double FACTEUR = 0.001;


    public Resize(Workspace w){
        this.workspace = w;
    }

    @Override
    public void mousePressed(double x, double y) {
        mouseX = x;
        workspace.setCursor(Cursor.E_RESIZE);
    }

    @Override
    public void mouseDragged(double x, double y) {


        double newX = x - mouseX;

        List<Node> layers = workspace.getCurrentLayers();

        double newScale;
        for (Node node : layers) {
            newScale = node.getScaleX() + (newX * FACTEUR)*-1; //*-1 pour changer de sens gauche -> droite
            if(newScale>=0) {
                node.setScaleX(newScale);
                node.setScaleY(newScale);
                node.setScaleZ(newScale);
            }
        }

        mouseX = x;
    }

    @Override
    public void mouseReleased(double x, double y) {
        workspace.setCursor(Cursor.DEFAULT);
    }
}