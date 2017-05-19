package ch.heigvd.tool;

import ch.heigvd.layer.GEMMSCanvas;
import ch.heigvd.workspace.Workspace;
        import javafx.scene.Cursor;
import javafx.scene.Node;

import java.util.List;


public class Resize implements Tool {

    private final Workspace workspace;
    private double mouseX;
    private double mouseY;

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
    }

    @Override
    public void mousePressed(double x, double y) {
        mouseX = x;
        mouseY = y;
    }

    @Override
    public void mouseDragged(double x, double y) {

        double mouseX = x;
        double mouseY = y;

        List<Node> layers = workspace.getCurrentLayers();

        for (Node node : layers) {
            double scale = node.getScaleX();
            double newScale = scale + 
            node.setScaleX();
            node.getTransforms().add(new javafx.scene.transform.Rotate(Math.toDegrees(Math.atan2(yx,yy))));

        }    }

    @Override
    public void mouseReleased(double x, double y) {
        workspace.setCursor(Cursor.DEFAULT);
    }
}