package ch.heigvd.tool;

import ch.heigvd.layer.GEMMSCanvas;
import ch.heigvd.workspace.Workspace;
        import javafx.scene.Cursor;
import javafx.scene.Node;

import java.util.List;


public class Resize implements Tool {

    private final Workspace workspace;


    public Resize(Workspace w) {
        workspace = w;
    }



    private void changeNodeSize(List<Node> layers,double x, double y, double width, double height){
       for(Node n : layers){
           n.setLayoutX(x);
           n.setLayoutY(y);
           if (n instanceof GEMMSCanvas) {
               ((GEMMSCanvas) n).setWidth(width);
               ((GEMMSCanvas) n).setHeight(height);
           }

       }
    }

    @Override
    public void mousePressed(double x, double y) {

    }

    @Override
    public void mouseDragged(double x, double y) {

        double mouseX = x;
        double mouseY = y;

        changeNodeSize(workspace.getCurrentLayers(),mouseX-x,mouseY-y,2,3);
    }

    @Override
    public void mouseReleased(double x, double y) {
        workspace.setCursor(Cursor.DEFAULT);
    }
}