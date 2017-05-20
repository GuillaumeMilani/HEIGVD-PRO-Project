package ch.heigvd.dialog;

import ch.heigvd.workspace.Workspace;
import java.util.Optional;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.converter.IntegerStringConverter;


/**
 * Display a dialog that allow the user to set a resize.
 */
public class ResizeDialog {
    
    // Dialog
    private Dialog<Rectangle> dialog;
    
    
    /**
     * Constructor
     */
    public ResizeDialog(Workspace workspace) {
        dialog = new Dialog<>();

        dialog.setTitle("Resize workspace");

        // Set button
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Set field
        TextField width = new TextField();
        width.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));
        width.setText(String.valueOf(workspace.width()));
        
        TextField height = new TextField();
        height.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));
        height.setText(String.valueOf(workspace.height()));
        
        TextField offsetX = new TextField();
        offsetX.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));
        offsetX.setText("0");
        
        TextField offsetY = new TextField();
        offsetY.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));
        offsetY.setText("0");

        Label description = new Label("Document size");
        description.setFont(Font.font(null, FontWeight.BOLD, 13));
        grid.add(description, 0, 0);
        grid.add(new Label("Width:"), 0, 1);
        grid.add(width, 1, 1);
        grid.add(new Label("Height:"), 0, 2);
        grid.add(height, 1, 2);

        grid.add(new Label("Offset X:"), 0, 3);
        grid.add(offsetX, 1, 3);
        grid.add(new Label("Offset Y:"), 0, 4);
        grid.add(offsetY, 1, 4);

        dialog.getDialogPane().setContent(grid);

        // Request focus
        Platform.runLater(() -> width.requestFocus());

        Node loginButton = dialog.getDialogPane().lookupButton(ButtonType.OK);
        loginButton.setDisable(true);
        
        ChangeListener<String> listener = (ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            
            String w = width.textProperty().get();
            String h = height.textProperty().get();
            String offX = offsetX.textProperty().get();
            String offY = offsetY.textProperty().get();
            
            boolean widthNotValid = w.trim().isEmpty() || (w.matches("[-+]?\\d*\\.?\\d+") && Integer.valueOf(w) <= 0);
            boolean heightNotValid = h.trim().isEmpty() || (h.matches("[-+]?\\d*\\.?\\d+") && Integer.valueOf(h) <= 0);
            
            boolean offsetXNotValide = offX.trim().isEmpty();
            boolean offsetYNotValide = offY.trim().isEmpty();
            
            loginButton.setDisable(widthNotValid || heightNotValid || offsetXNotValide || offsetYNotValide);
        };

        // Field validation
        width.textProperty().addListener(listener);
        height.textProperty().addListener(listener);
        offsetX.textProperty().addListener(listener);
        offsetY.textProperty().addListener(listener);
        
        // Return result
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return new Rectangle(Integer.valueOf(offsetX.getText()), 
                                        Integer.valueOf(offsetY.getText()), 
                                        Integer.valueOf(width.getText()), 
                                        Integer.valueOf(height.getText()));
            }

            return null;
        });
    }
    
    /**
     * Display dialog
     *
     * @return an optional rectangle
     */
    public Optional<Rectangle> showAndWait() {
        return dialog.showAndWait();
    }
}
