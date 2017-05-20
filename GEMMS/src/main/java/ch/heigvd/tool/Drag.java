package ch.heigvd.tool;

import ch.heigvd.workspace.Workspace;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Transform;

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

        List<Node> layers = workspace.getCurrentLayers();

        double newX = x - mouseX;
        double newY = y - mouseY;

        for (Node node : layers) {

            Point3D p = new Point3D(newX, newY, 0);


            for (Transform t : node.getTransforms()) {

               try {
                    p = t.createInverse().inverseDeltaTransform(p);
                } catch (NonInvertibleTransformException ex) {
                }
            }

            node.setTranslateX(p.getX() + node.getTranslateX());
            node.setTranslateY(p.getY() + node.getTranslateY());
        }

        mouseX = x;
        mouseY = y;

    }

    @Override
    public void mouseReleased(double x, double y) {

    }
}
