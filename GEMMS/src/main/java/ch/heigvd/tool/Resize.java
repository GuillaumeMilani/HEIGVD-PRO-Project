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
    Scale s = new Scale();


//    private void changeNodeSize(List<Node> layers,double x, double y, double width, double height){
//       for(Node n : layers){
//           n.setLayoutX(x);
//           n.setLayoutY(y);
//           if (n instanceof GEMMSCanvas) {
//               ((GEMMSCanvas) n).setWidth(width);
//               ((GEMMSCanvas) n).setHeight(height);
//           }
//
//       }
//    }

    public Resize(Workspace w){
        this.workspace = w;
        s = new Scale();
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
            double scale = s.getX();
           double newScale = scale + newX*0.01;
           s.setX(newScale);
           s.setY(newScale);
           s.setZ(newScale);

        }

        mouseX = x;
        mouseY = y;
    }

    @Override
    public void mouseReleased(double x, double y) {
        workspace.setCursor(Cursor.DEFAULT);
    }
}