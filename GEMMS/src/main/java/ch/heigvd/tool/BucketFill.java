package ch.heigvd.tool;

import ch.heigvd.layer.GEMMSCanvas;
import ch.heigvd.workspace.Workspace;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.Stack;

/**
 * Created by Michael on 21.05.2017.
 */
public class BucketFill implements Tool {
    // Color to fill
    Color colorToFillWith;
    Workspace workspace;
    private final double GAMMA = 0.2;

    /**
     * Constructor. Sets the default usage values which are a the color black and
     * a size of 1px.
     *
     * @param workspace the Workspace to work on
     */
    public BucketFill(Workspace workspace) {
        this(workspace, ColorSet.getInstance().getColor());
    }

    /**
     * Constructor. Specifies the color and size.
     *
     * @param workspace       the Workspace to work on
     * @param colorToFillWith the color of the bucket
     */
    public BucketFill(Workspace workspace, Color colorToFillWith) {
        this.workspace = workspace;
        this.colorToFillWith = colorToFillWith;
    }


    protected void drawPixel(int x, int y, GraphicsContext gc) {
        gc.setFill(colorToFillWith);
        int size = 10;
        gc.fillOval(x ,y, size   , size);
    }


    public void fill(Point2D begin, Color color, GEMMSCanvas canvas) {
        WritableImage wi = new WritableImage((int) canvas.getBoundsInParent().getWidth(), (int) canvas.getBoundsInParent().getHeight());
        WritableImage snapshot = canvas.snapshot(new SnapshotParameters(), wi);
        PixelReader pr = snapshot.getPixelReader();
        PixelWriter pw = canvas.getGraphicsContext2D().getPixelWriter();

        Stack<Point2D> stack = new Stack<>();
        stack.push(begin);

        Color colorBegin = pr.getColor((int)begin.getX(),(int)begin.getY());
        //tant que la stack n'est pas vide, on ajoute tous les voisins de la meme couleurs et on colorit
        while (!stack.isEmpty()) {
            System.out.println("size: " + stack.size());
            Point2D currentPoint = stack.pop();
            int currentPointX = (int) currentPoint.getX();
            int currentPointY = (int) currentPoint.getY();

            if (filled(pr, currentPointX, currentPointY)) { //condition de coloriage
                continue;
            }
            drawPixel(currentPointX,currentPointY,canvas.getGraphicsContext2D());

//            pw.setColor(currentPointX, currentPointY, color);

            pushIntoStack(stack, currentPointX - 1, currentPointY - 1, wi);
            pushIntoStack(stack, currentPointX - 1, currentPointY, wi);
            pushIntoStack(stack, currentPointX - 1, currentPointY + 1, wi);
            pushIntoStack(stack, currentPointX, currentPointY + 1, wi);
            pushIntoStack(stack, currentPointX + 1, currentPointY + 1, wi);
            pushIntoStack(stack, currentPointX + 1, currentPointY, wi);
            pushIntoStack(stack, currentPointX + 1, currentPointY - 1, wi);
            pushIntoStack(stack, currentPointX, currentPointY - 1, wi);


        }
        System.out.println("FINI");
    }

    private void pushIntoStack(Stack<Point2D> stack, int x, int y, WritableImage wi) {
        if (x > 0 || x < wi.getWidth() || y > 0 || y < wi.getHeight()) {
            stack.push(new Point2D(x, y));
        }
    }

    private boolean filled(PixelReader pr, int x, int y) {
        Color color = pr.getColor(x, y);
        return !isInInterval(color,colorToFillWith,GAMMA);
    }

    //Use to know if we color the pixel and neighbourg or not
    private boolean isInInterval(Color color, Color colorToFillWith, double gamma) {
        return isInInterval(color.getRed(), colorToFillWith.getRed(), gamma)
                && isInInterval(color.getBlue(), colorToFillWith.getBlue(), gamma)
                && isInInterval(color.getGreen(), colorToFillWith.getGreen(), gamma);
                //&& isInInterval(color.getOpacity(), colorToFillWith.getOpacity(), gamma);
    }

    private boolean isInInterval(double color, double colorToFillWith, double gamma) {
        return Math.abs(color - colorToFillWith) < gamma;
    }


    /**
     * Set the color of the brush.
     *
     * @param color the new Color
     */
    public void setColor(Color color) {
        this.colorToFillWith = color;
    }

    /**
     * Get the current brush color.
     *
     * @return the color of the brush
     */
    public Color getColor() {
        return colorToFillWith;
    }


    @Override
    public void mousePressed(double x, double y) {
        List<Node> layers = workspace.getCurrentLayers();
        for (Node n : layers) {
            if (n instanceof GEMMSCanvas) {
                fill(new Point2D(x, y), colorToFillWith, (GEMMSCanvas) n);
            }
        }
    }

    /**
     * Method to call during the dragging motion.
     *
     * @param x the event x coordinate
     * @param y the event y coordinate
     */
    @Override
    public void mouseDragged(double x, double y) {

    }

    /**
     * Method to call at the end of the drag movement.
     *
     * @param x the x coordinate of the event
     * @param y the y coordinate of the event
     */
    @Override
    public void mouseReleased(double x, double y) {

    }

}
