package ch.heigvd.tool;

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

    public Rotate(Workspace w){
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
        double centerX;
        double centerY;
        for (Node node : layers) {
             centerX = node.getBoundsInParent().getWidth()/2;
             centerY = node.getBoundsInParent().getHeight()/2;
            //System.out.println("cx : "+ centerX + " , cy : "+centerY);
            double yx = node.getLocalToSceneTransform().getMyx();
            double yy = node.getLocalToSceneTransform().getMyy();

            System.out.println("yy:"+yy);
            //node.getTransforms().add(new javafx.scene.transform.Rotate(getAngle(mouseX,mouseY,newX,newY,centerX,centerY),centerX,centerY,0));
            node.getTransforms().add(new javafx.scene.transform.Rotate(Math.toDegrees(Math.atan2(yx,yy))));

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
