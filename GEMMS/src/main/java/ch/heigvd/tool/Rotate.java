package ch.heigvd.tool;

import ch.heigvd.layer.IGEMMSNode;
import ch.heigvd.workspace.Workspace;
import javafx.scene.Node;

import java.util.List;

/**
 * Created by Michael on 17.05.2017.
 */
public class Rotate implements Tool{
    private double mouseX;
    private double mouseY;
    private Workspace workspace;

    List<Node> layers;

    public Rotate(Workspace w){
        this.workspace = w;


    }
    @Override
    public void mousePressed(double x, double y) {
        mouseX = x;
        mouseY = y;
        layers = workspace.getCurrentLayers();
//        for (Node node : layers) {
//
//            pivotX = node.getBoundsInLocal().getWidth() / 2;
//            pivotY = node.getBoundsInLocal().getHeight() / 2;
//            ((IGEMMSNode)node).rotateX.setPivotX(pivotX);
//            ((IGEMMSNode)node).rotateY.setPivotY(pivotY);
//
//        }
    }

    @Override
    public void mouseDragged(double x, double y) {

        double newX = x - mouseX;
        double newY = y - mouseY;

        double centerX;
        double centerY;
        for (Node node : layers) {

          //  node.setRotate(angle);
            ((IGEMMSNode)node).rotateY.setAngle((((IGEMMSNode)node).rotateY.getAngle() - newX ));
            ((IGEMMSNode)node).rotateX.setAngle((((IGEMMSNode)node).rotateX.getAngle() - newY ));
//            ((IGEMMSNode)node).rotateY.setAngle((angle));

           // ((IGEMMSNode)node).rotateZ.setAngle(((IGEMMSNode)node).rotateZ.getAngle() - newX);

//            centerX = node.getBoundsInParent().getWidth()/2;
//            centerY = node.getBoundsInParent().getHeight()/2;
//            double yx ;
//            double yy ;
//            double angle;
//            if(node.getTransforms().isEmpty()){ //si le noeud n'a pas encore eu de transformation on le rotate normalement
//                yx = node.getScene().getX();
//                yy = node.getScene().getY();
//            }else{
//                 yx = node.getLocalToSceneTransform().getMyx();
//                 yy = node.getLocalToSceneTransform().getMyy();
//                 //convert to degrees
//
//            }
//            angle = (Math.atan2(yx,yy));
//          //  angle = Math.toDegrees(angle);
//           // angle = angle < 0 ? angle + 360 : angle;
//
//
//            //node.getTransforms().add(new javafx.scene.transform.Rotate(getAngle(mouseX,mouseY,newX,newY,centerX,centerY),centerX,centerY,0));
//            node.getTransforms().add(new javafx.scene.transform.Rotate(angle,centerX,centerY));

        }

        mouseX = x;
        mouseY = y;

    }

    @Override
    public void mouseReleased(double x, double y) {

    }

    public double getAngle(double pointAX,double pointAY, double pointBX,double pointBY, double centreX, double centreY){
        double pointAC = Math.sqrt(square(centreX-pointAX)+square(centreY-pointAY));
        double pointBC = Math.sqrt(square(centreX-pointBX)+square(centreY-pointBY));
        double pointAB = Math.sqrt(square(pointBX-pointAX)+square(pointBY-pointAY));

        return Math.acos((pointBC*pointBC+pointAC*pointAC-pointAB*pointAB)/(2*pointAC*pointBC));
    }

    public double square(double x){
        return x*x;
    }
}
