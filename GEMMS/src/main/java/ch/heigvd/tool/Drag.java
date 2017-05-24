package ch.heigvd.tool;

import ch.heigvd.workspace.Workspace;
import javafx.geometry.Point3D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;


public class Drag implements Tool{

    private Workspace workspace;

    private double lastX;
    private double lastY;
    private boolean isAlignementActive;
    private final double ALIGNEMENT_DELTA = 20;

    public Drag(Workspace w){
        this.workspace = w;
        isAlignementActive = true;
    }

    @Override
    public void mousePressed(double x, double y) {
        workspace.setCursor(Cursor.CLOSED_HAND);

        lastX = x;
        lastY = y;
    }

    private void setPosition(double x, double y, Node n){
        n.setTranslateX(x);
        n.setTranslateY(y);
    }

    @Override
    public void mouseDragged(double x, double y) {

        double offsetX = x - lastX;
        double offsetY = y - lastY;

        for(Node n : workspace.getCurrentLayers()) {

            if(isAlignementActive){
                AnchorPane anchorPane = workspace.getLayerTool();
                double middleWorkspaceWidth = workspace.width()/2;
                double middleWorkspaceHeight = workspace.height()/2;
                double middleNodeWidth = (n.getBoundsInParent().getMinX() + n.getBoundsInParent().getMaxX())/2;
                double middleNodeHeight =(n.getBoundsInParent().getMinY() + n.getBoundsInParent().getMaxY())/2 ;

                if(Math.abs(x - middleWorkspaceWidth) < ALIGNEMENT_DELTA){
                    setPosition(middleWorkspaceWidth,y,n);

                }
                if(Math.abs(y - middleWorkspaceHeight) < ALIGNEMENT_DELTA){
                    setPosition(x,middleWorkspaceHeight,n);

                }else{
                    n.setTranslateX(n.getTranslateX() + offsetX);
                    n.setTranslateY(n.getTranslateY() + offsetY);
                }
            }else {
                n.setTranslateX(n.getTranslateX() + offsetX);
                n.setTranslateY(n.getTranslateY() + offsetY);
            }
        }

        lastX = x;
        lastY = y;
    }

    @Override
    public void mouseReleased(double x, double y) {
        workspace.setCursor(Cursor.DEFAULT);

    }
}