package ch.heigvd.tool;

import ch.heigvd.workspace.Workspace;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Cursor;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Crop  implements Tool {
    
    private final Rectangle rectangle;
    private final Workspace workspace;
    
    private double lastX;
    private double lastY;
    
    private boolean isMoving;
    private boolean isDragging;
    
    private boolean isMoved;
    
    public Crop(Workspace w) {
        workspace = w;
        
        // Set the cursor
        workspace.getLayerTool().setCursor(Cursor.CROSSHAIR);

        // Create selection box
        rectangle = new Rectangle(0, 0, 0, 0);
        rectangle.setVisible(true);
        rectangle.setFill(Color.TRANSPARENT);
        rectangle.setStroke(Color.BLACK);
        rectangle.setStrokeWidth(1.8);
    }

    @Override
    public void mousePressed(double x, double y) {
        
        Point3D p = PositionMapper.convert(rectangle, new Point3D(x, y, 0));
        
        if (!rectangle.contains(new Point2D(p.getX(), p.getY()))) {
            
            workspace.getLayerTool().getChildren().remove(rectangle);
            workspace.getLayerTool().getChildren().add(rectangle);
            rectangle.setWidth(0);
            rectangle.setHeight(0);
            rectangle.setX(x);
            rectangle.setY(y);
            rectangle.setVisible(false);
            
            workspace.getLayerTool().setCursor(Cursor.NE_RESIZE);

            isDragging = true;
        }
        else {
            workspace.getLayerTool().setCursor(Cursor.MOVE);
            
            isMoving = true;
        }

        lastX = x; 
        lastY = y;
    }

    @Override
    public void mouseDragged(double x, double y) {
        if(isDragging) {
            double width = x - rectangle.getX();
            double height = y - rectangle.getY();

            if (width < 0) {
                rectangle.setTranslateX(x - rectangle.getX());
            } else {
                rectangle.setTranslateX(0);
            }

            if (height < 0) {
                rectangle.setTranslateY(y - rectangle.getY());
            } else {
                rectangle.setTranslateY(0);
            }

            rectangle.setWidth(Math.abs(width));
            rectangle.setHeight(Math.abs(height));
            
            isMoved = true;
            rectangle.setVisible(true);
        }
        
        else if(isMoving) {
            double addX = x - lastX;
            double addY = y - lastY;

            rectangle.setTranslateX(rectangle.getTranslateX() + addX);
            rectangle.setTranslateY(rectangle.getTranslateY() + addY);
            
            lastX = x; 
            lastY = y;
            
            isMoved = true;
        }
    }

    @Override
    public void mouseReleased(double x, double y) {
        
        Point3D p = PositionMapper.convert(rectangle, new Point3D(x, y, 0));

        if(!isMoved && 
                rectangle.contains(new Point2D(p.getX(), p.getY())) && 
                rectangle.getWidth() > 0 && 
                rectangle.getHeight() > 0) {

            workspace.resizeCanvas((int)rectangle.getWidth(), (int)rectangle.getHeight(), -(int)rectangle.getBoundsInParent().getMinX(), -(int)rectangle.getBoundsInParent().getMinY());
            rectangle.setWidth(0);
            rectangle.setHeight(0);
            rectangle.setX(0);
            rectangle.setY(0);
        }
        
        workspace.getLayerTool().setCursor(Cursor.CROSSHAIR);
        
        isDragging = false;
        isMoving = false;
        isMoved = false;
    }
    
}
