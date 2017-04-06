package ch.heigvd.gemms;

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
import javafx.scene.Node;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

/**
 * <h1></h1>
 *
 *
 */
public class Document {

    private final FileChooser fileChooser;
    private final Stage stage;
    private File currentFile;

    /**
     * Constructor
     *
     * @param s
     */
    public Document(Stage s) {
        stage = s;

        fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home")));
    }

    /**
     *
     * @throws IOException
     * @throws FileNotFoundException
     * @throws ClassNotFoundException
     *
     */
    Node open() throws FileNotFoundException, IOException, ClassNotFoundException {

        fileChooser.setTitle("Open file");
        fileChooser.getExtensionFilters().add(
                new ExtensionFilter("GEMMS", "*.gemms"));

        currentFile = fileChooser.showOpenDialog(stage);
        if (currentFile != null) {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(currentFile))) {
                return (Node) in.readObject();
            }
        }

        return null;
    }

    /**
     *
     * @throws IOException
     * @throws FileNotFoundException
     */
    void save(Node n) throws FileNotFoundException, IOException {
        
        if (currentFile != null) {
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(currentFile))) {
                out.writeObject(n);
            }
        } else {
            saveAs(n);
        }
    }

    /**
     *
     * @throws IOException
     * @throws FileNotFoundException
     */
    void saveAs(Node workspace) throws FileNotFoundException, IOException {

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
            } else {
                // throw new Exception(currentFile.getName() + " has no valid file-extension.");
            }
        }
    }

    /**
     *
     * @throws IOException
     */
    void export(Node workspace) throws IOException {

        fileChooser.setTitle("Export");
        fileChooser.getExtensionFilters().add(
                new ExtensionFilter("png files (*.png)", "*.png"));
        fileChooser.setInitialFileName("*.png");

        // TODO : Add other extensions and check file name
        // new ExtensionFilter("All Files", "*.*")
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            // TODO : Set the size with the workspace
            WritableImage writableImage = new WritableImage(800, 600);
            workspace.snapshot(null, writableImage);
            RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
            ImageIO.write(renderedImage, "png", file);
        }
    }
}
