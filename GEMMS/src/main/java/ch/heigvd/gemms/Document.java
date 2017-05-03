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
import java.util.Optional;
import javafx.application.Platform;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Pair;
import javafx.util.converter.IntegerStringConverter;

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
     * @param pane
     * @return 
     */
    public Workspace newDocument(Pane pane) {
        // Create dialog
        Dialog<Pair<Integer, Integer>> dialog = new Dialog<>();
        dialog.setTitle("Create a new file");

        // Set button
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Set text field
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField width = new TextField();
        width.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));
        TextField height = new TextField();
        height.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));

        grid.add(new Label("Width:"), 0, 0);
        grid.add(width, 1, 0);
        grid.add(new Label("Height:"), 0, 1);
        grid.add(height, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // Request focus
        Platform.runLater(() -> width.requestFocus());

        Node loginButton = dialog.getDialogPane().lookupButton(ButtonType.OK);
        loginButton.setDisable(true);

        // Field validation
        width.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty());
        });

        height.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty());
        });

        // Return result
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return new Pair<>(Integer.valueOf(width.getText()), Integer.valueOf(height.getText()));
            }

            return null;
        });

        Optional<Pair<Integer, Integer>> result = dialog.showAndWait();

        if(result.isPresent()) {
            return new Workspace(result.get().getKey(), result.get().getValue(), pane);
        }

        return null;
    }

    /**
     *
     * @throws IOException
     * @throws FileNotFoundException
     * @throws ClassNotFoundException
     *
     */
    Workspace open() throws FileNotFoundException, IOException, ClassNotFoundException {

        fileChooser.setTitle("Open file");
        fileChooser.getExtensionFilters().add(
                new ExtensionFilter("GEMMS", "*.gemms"));

        currentFile = fileChooser.showOpenDialog(stage);
        if (currentFile != null) {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(currentFile))) {
                return (Workspace) in.readObject();
            }
        }

        return null;
    }

    /**
     *
     * @throws IOException
     * @throws FileNotFoundException
     */
    void save(Workspace n) throws FileNotFoundException, IOException {
        
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
    void saveAs(Workspace workspace) throws FileNotFoundException, IOException {

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
    void export(Workspace workspace) throws IOException {

        fileChooser.setTitle("Export");
        fileChooser.getExtensionFilters().add(
                new ExtensionFilter("png files (*.png)", "*.png"));
        fileChooser.setInitialFileName("*.png");

        // TODO : Add other extensions and check file name
        // new ExtensionFilter("All Files", "*.*")
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            // TODO : Set the size with the workspace
            WritableImage writableImage = new WritableImage((int)workspace.getWidth(), (int)workspace.getHeight());
            workspace.snapshot(null, writableImage);
            RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
            ImageIO.write(renderedImage, "png", file);
        }
    }
}
