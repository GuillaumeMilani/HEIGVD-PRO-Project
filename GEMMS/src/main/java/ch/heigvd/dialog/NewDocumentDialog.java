package ch.heigvd.dialog;

import java.util.Optional;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Pair;
import javafx.util.converter.IntegerStringConverter;

/**
 * Display a dialog that allow the user to set a size (width, height).
 */
public class NewDocumentDialog {

    // Dialog
    private Dialog<Pair<Integer, Integer>> dialog;

    /**
     * Constructor
     */
    public NewDocumentDialog() {
        dialog = new Dialog<>();

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

        Label description = new Label("Document size");
        description.setFont(Font.font(null, FontWeight.BOLD, 13));
        grid.add(description, 0, 0);
        grid.add(new Label("Width:"), 0, 1);
        grid.add(width, 1, 1);
        grid.add(new Label("Height:"), 0, 2);
        grid.add(height, 1, 2);

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
    }

    /**
     * Display dialog
     *
     * @return an optional pair with width and height
     */
    public Optional<Pair<Integer, Integer>> showAndWait() {
        return dialog.showAndWait();
    }
}
