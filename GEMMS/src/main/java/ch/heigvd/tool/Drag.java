package ch.heigvd.tool;

import ch.heigvd.workspace.Workspace;
import javafx.scene.Cursor;
import javafx.scene.Node;

public class Drag extends AbstractTool {

    private double lastX;
    private double lastY;

    public Drag(Workspace w){
        super(w);
    }
    @Override
    public void mousePressed(double x, double y) {
        workspace.setCursor(Cursor.CLOSED_HAND);

        lastX = x;
        lastY = y;
    }

    @Override
    public void mouseDragged(double x, double y) {

        double offsetX = x - lastX;
        double offsetY = y - lastY;

        for(Node n : workspace.getCurrentLayers()) {

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
}