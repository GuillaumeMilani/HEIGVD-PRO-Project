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

import java.util.LinkedList;
import java.util.List;

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


    private void drawPixel(int x, int y, GraphicsContext gc) {
        gc.setFill(colorToFillWith);
        int size = 10;
        gc.fillOval(x , y , size, size);
    }


    public void fill(Point2D begin, Color color, GEMMSCanvas canvas) {
        colorToFillWith = (ColorSet.getInstance().getColor());

        WritableImage wi = new WritableImage((int) canvas.getBoundsInParent().getWidth(), (int) canvas.getBoundsInParent().getHeight());
        WritableImage snapshot = canvas.snapshot(new SnapshotParameters(), wi);
        PixelReader pr = snapshot.getPixelReader();
        PixelWriter pw = snapshot.getPixelWriter();

        Color colorBegin = pr.getColor((int)begin.getX(),(int)begin.getY());
        LinkedList<Point2D> fifo = new  LinkedList<>();
        fifo.add(begin);

        if(colorToFillWith.equals(colorBegin)){
            return;
        }


        //tant que la fifo n'est pas vide, on ajoute tous les voisins de la meme couleurs et on colorit
        while (!fifo.isEmpty() ) {
            Point2D currentPoint = fifo.removeFirst();
            int currentPointX = (int) currentPoint.getX();
            int currentPointY = (int) currentPoint.getY();
            Color pixelColor = pr.getColor(currentPointX,currentPointY);

            if (isSameColor(pixelColor,colorBegin)) { //Si la couleur du départ et celle de maintenant sont les memes on ajoutes
              continue;
            }

            pw.setColor(currentPointX, currentPointY, colorToFillWith);
            pushIntoFifo(fifo, currentPointX, currentPointY + 1, canvas, pr,colorBegin);
            pushIntoFifo(fifo, currentPointX - 1, currentPointY, canvas, pr,colorBegin);
            pushIntoFifo(fifo, currentPointX + 1, currentPointY, canvas, pr,colorBegin);
            pushIntoFifo(fifo, currentPointX, currentPointY - 1, canvas, pr,colorBegin);


        }
        canvas.getGraphicsContext2D().drawImage(snapshot,0,0);
    }

    private void pushIntoFifo( LinkedList<Point2D> fifo, int x, int y, Node wi,PixelReader pr, Color colored) {
        if (x > 0 && x < wi.getBoundsInParent().getWidth() && y > 0 && y <  wi.getBoundsInParent().getHeight()) {
        fifo.add(new Point2D(x, y));
        }
    }

    private boolean isSameColor(Color pixelColor, Color colorBegin) {
        return !isSameColorInInterval(pixelColor,colorBegin,GAMMA);
    }

    //Use to know if we color the pixel and neighbourg or not
    private boolean isSameColorInInterval(Color color, Color colorToFillWith, double gamma) {
        return isInInterval(color.getRed(), colorToFillWith.getRed(), gamma)
                && isInInterval(color.getBlue(), colorToFillWith.getBlue(), gamma)
                && isInInterval(color.getGreen(), colorToFillWith.getGreen(), gamma);
                //&& isInInterval(color.getOpacity(), colorToFillWith.getOpacity(), gamma);
    }
    
    private boolean isInInterval(double a, double b, double gamma) {
        return Math.abs(a - b) < gamma;
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
