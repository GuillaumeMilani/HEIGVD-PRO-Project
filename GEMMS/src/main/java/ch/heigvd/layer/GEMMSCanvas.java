package ch.heigvd.layer;

import ch.heigvd.gemms.CSSIcons;
import ch.heigvd.workspace.LayerListable;
import java.io.IOException;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javafx.geometry.Point3D;
import javafx.scene.SnapshotParameters;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;

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

        sp.setTransform(getLocalToSceneTransform());
        
        WritableImage writableImage = new WritableImage(width, height);
        snapshot(sp, writableImage);

        // Get a pixel reader
        PixelReader pixelReader = writableImage.getPixelReader();

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

        // Write scale info
        s.writeDouble(getScaleX());
        s.writeDouble(getScaleY());
        s.writeDouble(getScaleZ());

        // Write rotate info
        s.writeDouble(getRotate());
        s.writeDouble(getRotationAxis().getX());
        s.writeDouble(getRotationAxis().getY());
        s.writeDouble(getRotationAxis().getZ());
        
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
        
        // Set scale info
        setScaleX(s.readDouble());
        setScaleY(s.readDouble());
        setScaleZ(s.readDouble());
        
        // Set rotate info
        setRotate(s.readDouble());
        setRotationAxis(new Point3D(s.readDouble(), s.readDouble(), s.readDouble()));
       
    }

    @Override
    public String getLayerName() {
        return "Canvas";
    }

    @Override
    public String getThumbnailClass() {
        return CSSIcons.CANVAS;
    }

    @Override
    public IGEMMSNode clone() {
        GEMMSCanvas newCanvas = new GEMMSCanvas(getWidth(), getHeight());

        // Get an image
        SnapshotParameters sp = new SnapshotParameters();
        sp.setFill(Color.TRANSPARENT);

        sp.setTransform(getLocalToSceneTransform());

        WritableImage writableImage = new WritableImage((int)Math.round(getWidth()), (int)Math.round(getHeight()));
        snapshot(sp, writableImage);

        // Get a pixel reader
        PixelReader pixelReader = writableImage.getPixelReader();

        // Get a pixel writer
        PixelWriter pixelWriter = newCanvas.getGraphicsContext2D().getPixelWriter();

        // Copy the color of every pixel
        for (int y = 0; y < getHeight(); ++y) {
            for (int x = 0; x < getWidth(); ++x) {
                Color c = pixelReader.getColor(x, y);

                pixelWriter.setColor(x, y, c);
            }
        }

        // Copy translate info
        newCanvas.setTranslateX(getTranslateX());
        newCanvas.setTranslateY(getTranslateY());
        newCanvas.setTranslateZ(getTranslateZ());

        // Copy scale info
        newCanvas.setScaleX(getScaleX());
        newCanvas.setScaleY(getScaleY());
        newCanvas.setScaleZ(getScaleZ());

        // Copy rotate info
        newCanvas.setRotate(getRotate());
        Point3D rotationAxis = new Point3D(getRotationAxis().getX(), getRotationAxis().getY(), getRotationAxis().getZ());
        newCanvas.setRotationAxis(rotationAxis);

        return newCanvas;
    }
}
