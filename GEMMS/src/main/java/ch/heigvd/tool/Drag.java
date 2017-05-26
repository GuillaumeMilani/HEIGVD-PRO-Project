package ch.heigvd.tool;

import ch.heigvd.workspace.Workspace;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

import java.util.List;

public class Drag extends AbstractTool {

    //The old coordinates
    private double lastX;
    private double lastY;
      //The list of selected Nodes
    private List<Node> layers;
    private boolean isAlignementActive;
    private AnchorPane anchorPane;

    public Drag(Workspace w){
              super(w);

        this.workspace = w;
        this.isAlignementActive = false;
        this.anchorPane = workspace.getLayerTool();
    }

    @Override
    public void mousePressed(double x, double y) {
        workspace.setCursor(Cursor.CLOSED_HAND);

        lastX = x;
        lastY = y;
        layers = workspace.getCurrentLayers();
    }

    private void setPosition(double x, double y, Node n){
        n.setTranslateX(x);
        n.setTranslateY(y);
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

    public void turnAlignementOnOff(){
        isAlignementActive = !isAlignementActive;
        if(isAlignementActive){
            printAlignement();
        }else{
            anchorPane.getChildren().clear();
        }
    }

    private void printAlignement(){
        Canvas alignement = new Canvas(workspace.width(),workspace.height());
        GraphicsContext gc = alignement.getGraphicsContext2D();
        gc.setStroke(Color.GREEN);
        //Lignes principales
        gc.setLineWidth(2);
        gc.strokeLine(workspace.width()/2, 0, workspace.width()/2, workspace.height());
        gc.strokeLine(0, workspace.height()/2, workspace.height(), workspace.height()/2);

        //Lignes secondaires
        gc.setLineWidth(1);
        gc.strokeLine(workspace.width()/4, 0, workspace.width()/4, workspace.height());
        gc.strokeLine(workspace.width()*3/4, 0, workspace.width()*3/4, workspace.height());
        gc.strokeLine(0, workspace.height()/4, workspace.height(), workspace.height()/4);
        gc.strokeLine(0, workspace.height()/4*3, workspace.height(), workspace.height()*3/4);

        anchorPane.getChildren().add(alignement);
    }

    public boolean isAlignementActive(){
        return isAlignementActive;
    }
}