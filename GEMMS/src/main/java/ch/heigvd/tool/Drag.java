package ch.heigvd.tool;

import ch.heigvd.workspace.Workspace;
import javafx.geometry.Point3D;
import javafx.scene.Cursor;
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
        workspace.setCursor(Cursor.CLOSED_HAND);
        mouseX = x;
        mouseY = y;
    }

    @Override
    public void mouseDragged(double x, double y) {

        List<Node> layers = workspace.getCurrentLayers();



        for (Node node : layers) {
            //Essaie3
            Point3D point = PositionMapper.convert(node, x, y, 0);
            Point3D point2 = PositionMapper.convert(node, mouseX, mouseY, 0);

            double newX =x - point2.getX();
            double newY = y - point2.getY();
            node.setTranslateX(newX + point.getX());
            node.setTranslateY(newY + point.getY());

            //Essaie 2
//            Point3D point = PositionMapper.convert(node, x, y, 0);
//            Point3D point2 = PositionMapper.convert(node, mouseX, mouseY, 0);
//
//            double newX = point.getX() - point2.getX();
//            double newY = point.getY() - point2.getY();
//            node.setTranslateX(newX + node.getTranslateX());
//            node.setTranslateY(newY + node.getTranslateY());
//
            //Essaie 1
//            Point3D p = new Point3D(newX, newY, 0);
//
//
//            for (Transform t : node.getTransforms()) {
//
//               try {
//                    p = t.createInverse().inverseDeltaTransform(p);
//                } catch (NonInvertibleTransformException ex) {
//                   ex.printStackTrace();
//                }
//            }
//            node.setTranslateX(p.getX() + node.getTranslateX());
//            node.setTranslateY(p.getY() + node.getTranslateY());

        }

        mouseX = x;
        mouseY = y;

    }

    @Override
    public void mouseReleased(double x, double y) {
        workspace.setCursor(Cursor.DEFAULT);

    }


}
