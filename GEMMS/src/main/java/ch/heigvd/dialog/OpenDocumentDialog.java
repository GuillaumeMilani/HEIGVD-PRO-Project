package ch.heigvd.dialog;

import java.io.File;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Display a dialog that allow the user to open a project file. (*.gemms)
 */
public class OpenDocumentDialog {
    
    private final FileChooser fileChooser;
    private final Stage stage;
    
    /**
     * Constructor
     * 
     * @param s stage for FileChooser
     */
    public OpenDocumentDialog(Stage s) {
        stage = s;
        
        // Init file chooser
        fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.setTitle("Open file");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("GEMMS", "*.gemms"));

        
    }
    
    /**
     * Display dialog
     * 
     * @return File
     */
    public File showAndWait() {
        return fileChooser.showOpenDialog(stage);
    }
}
