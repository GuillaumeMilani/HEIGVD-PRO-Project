
package ch.heigvd.layer;

import ch.heigvd.gemms.CSSIcons;
import ch.heigvd.workspace.LayerListable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class GEMMSImage  extends javafx.scene.image.ImageView implements IGEMMSNode, LayerListable {
    
    public GEMMSImage() {
        super();
    }
    
    public GEMMSImage(Image image) {
        super(image);
    }
    
    public GEMMSImage(String url) {
        super(url);
    }
    
    private void writeObject(ObjectOutputStream s) throws IOException {
        
        // Get image
        Image image = getImage();
        
        // Write the size
        int height = (int)image.getHeight();
        int width = (int)image.getWidth();
        s.writeInt(width);
        s.writeInt(height);

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
        
        // Write viewport
        Rectangle2D r = getViewport();
        s.writeDouble(r.getMinX());
        s.writeDouble(r.getMinY());
        s.writeDouble(r.getWidth());
        s.writeDouble(r.getHeight());
    }
    
    private void readObject(ObjectInputStream s) throws IOException {
        // Get image size
        int width = s.readInt();
        int height = s.readInt();
        
        // Create an empty image
        WritableImage w = new WritableImage(width, height);

        // Get the pixel writer
        PixelWriter pixelWriter = w.getPixelWriter();

        // Set the color every pixel of this image
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                Color c = new Color(s.readDouble(), s.readDouble(), s.readDouble(), s.readDouble());
                pixelWriter.setColor(x, y, c);
            }
        }
        
        setImage(w);
        
        setViewport(new Rectangle2D(s.readDouble(), s.readDouble(), s.readDouble(), s.readDouble()));
    }

    @Override
    public String getLayerName() {
        return "Image";
    }

    @Override
    public String getThumbnailClass() {
        return CSSIcons.IMAGE;
    }
}
