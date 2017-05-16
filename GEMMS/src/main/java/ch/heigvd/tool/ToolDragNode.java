package ch.heigvd.tool;

import ch.heigvd.workspace.Workspace;
import javafx.scene.Cursor;
import javafx.scene.Node;

import java.util.List;

/**
 * Created by Michael on 15.05.2017.
 */
public class ToolDragNode implements Tool{

    // The workspace that is currently working
    private Workspace workspace;
    private CordRelative cordRelative;


    /**
     * Constructor. Sets the default usage values which are a the color black
     * and a size of 1px.
     * @param workspace the Workspace to work on
     */
    public ToolDragNode(Workspace workspace) {
        this.workspace = workspace;
        cordRelative = new CordRelative();
    }

//    @Override
//    public void enableDrag() {
//        CordRelative cordRelative = new CordRelative();
//        setOnMousePressed(mouseEvent -> {
//            cordRelative.x = mouseEvent.getX();
//            cordRelative.y = mouseEvent.getY();
//            getScene().setCursor(Cursor.MOVE);
//        });
//
//        setOnMouseDragged(mouseEvent -> {
//            double newX = getLayoutX() + mouseEvent.getX() - cordRelative.x;
//            double newY = getLayoutY() + mouseEvent.getY() - cordRelative.y;
//            if(newX < getScene().getWidth() && newX > 0){
//                setLayoutX(newX);
//            }
//            if(newY < getScene().getHeight() && newY > 0){
//                setLayoutY(newY);
//            }
//        });
//        setOnMouseReleased(mouseEvent -> getScene().setCursor(Cursor.HAND));
//
//        setOnMouseEntered(mouseEvent -> {
//            if(!mouseEvent.isPrimaryButtonDown()){
//                getScene().setCursor(Cursor.HAND);
//            }
//        });
//
//        setOnMouseExited(mouseEvent -> {
//            if(!mouseEvent.isPrimaryButtonDown()){
//                getScene().setCursor(Cursor.DEFAULT);
//            }
//        });
//    }

    @Override
    public void mousePressed(double x, double y) {
        cordRelative.x = x;
        cordRelative.y = y;
        workspace.setCursor(Cursor.MOVE);
    }

    @Override
    public void mouseDragged(double x, double y) {
        // Get the selected layers of the workspace
        List<Node> layers = workspace.getCurrentLayers();
        double newX = workspace.getTranslateX() + x - cordRelative.x;
        double newY = workspace.getTranslateY() + y - cordRelative.y;

        for (Node node : layers) {
            
                node.setTranslateX(newX);
                node.setTranslateY(newY);
        }
    }

    @Override
    public void mouseReleased(double x, double y) {
        workspace.setCursor(Cursor.DEFAULT);

    }


    //the relative coordonate to keep a trace of them
    private class CordRelative{
        double x;
        double y;
    }
}
