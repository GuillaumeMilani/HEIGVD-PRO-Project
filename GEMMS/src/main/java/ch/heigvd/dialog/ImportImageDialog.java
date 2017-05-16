package ch.heigvd.dialog;

import ch.heigvd.gemms.GEMMSStageFXMLController;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;

/**
 * Display a dialog that allow the user to open an image. (*.png or .jpg)
 */
public class ImportImageDialog {
    
    private final FileChooser fileChooser;
    private final Stage stage;
    
    /**
     * Constructor
     * 
     * @param s stage for FileChooser
     */
    public ImportImageDialog(Stage s) {
        stage = s;
        
        // Init file chooser
        fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        
        fileChooser.setTitle("Open image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("png files (*.png)", "*.png"),
                new FileChooser.ExtensionFilter("jpg files (*.jpg)", "*.jpg"));
    }
    
    /**
     * Display dialog
     * 
     * @return Image
     */
    public Image showAndWait() {
        File file = fileChooser.showOpenDialog(stage);
        
        Image image = null;
        
        // Convert file to image
        if(file != null) {
            try {
                BufferedImage bufferedImage = ImageIO.read(file);
                image = SwingFXUtils.toFXImage(bufferedImage, null);
            } catch (IOException ex) {
                Logger.getLogger(GEMMSStageFXMLController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return image;
    }
}
