package ch.heigvd.layer;

import ch.heigvd.gemms.CSSIcons;
import ch.heigvd.workspace.LayerListable;
import java.io.IOException;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javafx.scene.SnapshotParameters;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class GEMMSCanvas extends javafx.scene.canvas.Canvas implements IGEMMSNode, LayerListable {

    /**
     * Constructor
     *
     */
    public GEMMSCanvas() {
        super();
    }

    /**
     * Constructor
     *
     * @param width this is the width of this canvas
     * @param height this is the height of this canvas
     */
    public GEMMSCanvas(double width, double height) {
        super(width, height);
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();

        // Get the size
        int width = (int) getWidth();
        int height = (int) getHeight();

        // Write the size
        s.writeInt(width);
        s.writeInt(height);

        // Get an image 
        SnapshotParameters sp = new SnapshotParameters();
        sp.setFill(Color.TRANSPARENT);
        WritableImage writableImage = new WritableImage(width, height);
        snapshot(sp, writableImage);
        Image image = (Image) writableImage;

        // Get a pixel reader
        PixelReader pixelReader = image.getPixelReader();

        // Write the color of every pixel
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                Color c = pixelReader.getColor(x, y);

                s.writeDouble(c.getRed());
                s.writeDouble(c.getGreen());
                s.writeDouble(c.getBlue());
                s.writeDouble(c.getOpacity());
            }
        }
        
        // Write translate info
        s.writeDouble(getTranslateX());
        s.writeDouble(getTranslateY());
        s.writeDouble(getTranslateZ());
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {

        // Get the size of the canvas
        int width = s.readInt();
        int height = s.readInt();

        // Set the size of this canvas
        setWidth(width);
        setHeight(height);

        GraphicsContext gc = getGraphicsContext2D();

        // Get the pixel writer
        PixelWriter pixelWriter = gc.getPixelWriter();

        // Set the color every pixel of this canvas
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                Color c = new Color(s.readDouble(), s.readDouble(), s.readDouble(), s.readDouble());
                pixelWriter.setColor(x, y, c);
            }
        }
        
        // Set translate info
        setTranslateX(s.readDouble());
        setTranslateY(s.readDouble());
        setTranslateZ(s.readDouble());
    }





    @Override
    public String getLayerName() {
        return "Canvas";
    }

    @Override
    public String getThumbnailClass() {
        return CSSIcons.CANVAS;
    }
}
