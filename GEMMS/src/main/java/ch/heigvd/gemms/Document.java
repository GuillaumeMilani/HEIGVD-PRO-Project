package ch.heigvd.gemms;

import ch.heigvd.workspace.Workspace;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.imageio.ImageIO;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;


/**
 * <h1>Document</h1>
 *
 * This class create or load a from file a workspace. And allow to save in file 
 * and export as an image.
 */
public class Document {

    private Workspace workspace;
    
    private FileChooser fileChooser;
    private Stage stage;
    private File currentFile;
    
    private String name;
    
    /**
     * Constructor
     * 
     * Create a new document
     *
     * @param s stage for the file chooser
     * @param width width of the workspace
     * @param height height of the workspace
     */
    public Document(Stage s, int width, int height) {
        init(s);
        
        name = "untiled";
        workspace = new Workspace(width, height);
    }
    
    /**
     * Constructor
     * 
     * Open a new document with a file
     * 
     * @param s stage for the file chooser
     * @param f file to open
     * @throws FileNotFoundException
     * @throws IOException
     * @throws ClassNotFoundException 
     */
    public Document(Stage s, File f) throws FileNotFoundException, IOException, ClassNotFoundException {
        init(s);
        
        // TODO : Check file extension
        currentFile = f;
        
        name = currentFile.getName();
        
        if (currentFile != null) {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(currentFile))) {
                workspace = (Workspace) in.readObject();
            }
        }
    }
    
    /**
     * Init for constructor
     * 
     * @param s stage to affect
     */
    private void init(Stage s) {
        stage = s;

        fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home")));
    }
    

    /**
     * Save the workspace
     *
     * @throws IOException
     * @throws FileNotFoundException
     */
    void save() throws FileNotFoundException, IOException {
        
        if (currentFile != null) {
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(currentFile))) {
                out.writeObject(workspace);
            }
        } else {
            saveAs();
        }
    }

    /**
     * SaveAs the workspace
     *
     * @throws IOException
     * @throws FileNotFoundException
     */
    void saveAs() throws FileNotFoundException, IOException {

        fileChooser.setTitle("Save as");
        fileChooser.getExtensionFilters().add(
                new ExtensionFilter("GEMMS", "*.gemms"));
        fileChooser.setInitialFileName("*.gemms");

        currentFile = fileChooser.showSaveDialog(stage);
        if (currentFile != null) {
            if (currentFile.getName().endsWith(".gemms")) {
                try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(currentFile))) {
                    out.writeObject(workspace);
                }
                
                name = currentFile.getName();
            } else {
                // throw new Exception(currentFile.getName() + " has no valid file-extension.");
            }
        }
    }

    /**
     * Export the workspace as an image
     *
     * @throws IOException
     */
    void export() throws IOException {

        fileChooser.setTitle("Export");
        fileChooser.getExtensionFilters().add(
                new ExtensionFilter("png files (*.png)", "*.png"));
        fileChooser.setInitialFileName("*.png");

        // TODO : Add other extensions and check file name
        // new ExtensionFilter("All Files", "*.*")
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            WritableImage writableImage = new WritableImage((int)workspace.width(), (int)workspace.height());
            workspace.snapshot(null, writableImage);
            RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
            ImageIO.write(renderedImage, "png", file);
        }
    }
    
    /**
     * Return the workspace
     * 
     * @return the workspace
     */
    public Workspace workspace() {
        return workspace;
    }
    
    /**
     * Return name of the document
     * 
     * @return name
     */
    public String name() {
        return name;
    }
}
