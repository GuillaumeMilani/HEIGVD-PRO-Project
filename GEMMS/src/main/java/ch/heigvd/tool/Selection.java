package ch.heigvd.tool;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

// TODO : Move selection
public class Selection implements Tool {

    private final Scene scene;

    private Rectangle rectangle;

    // TODO : Layer and clipboard injection dependency
    public Selection(Scene s, Group root) {
        scene = s;
        scene.setCursor(Cursor.CROSSHAIR);

        rectangle = new Rectangle(0, 0, 20, 20);
        rectangle.setVisible(false);
        rectangle.setFill(Color.TRANSPARENT);
        rectangle.setStroke(Color.BLACK);
        rectangle.getStrokeDashArray().addAll(4d, 15d);

        // Stroke animation
        final double maxOffset
                = rectangle.getStrokeDashArray().stream()
                        .reduce(
                                0d,
                                (a, b) -> a + b
                        );

        Timeline timeline = new Timeline(
                new KeyFrame(
                        Duration.ZERO,
                        new KeyValue(
                                rectangle.strokeDashOffsetProperty(),
                                0,
                                Interpolator.LINEAR
                        )
                ),
                new KeyFrame(
                        Duration.seconds(2),
                        new KeyValue(
                                rectangle.strokeDashOffsetProperty(),
                                maxOffset,
                                Interpolator.LINEAR
                        )
                )
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        root.getChildren().add(rectangle);
    }

    @Override
    public void mousePressed(double x, double y) {
        rectangle.setVisible(true);
        rectangle.setWidth(0);
        rectangle.setHeight(0);
        rectangle.setX(x);
        rectangle.setY(y);
    }

    @Override
    public void mouseDragged(double x, double y) {
        double width = x - rectangle.getX();
        double height = y - rectangle.getY();

        if (width < 0) {
            rectangle.setTranslateX(x - rectangle.getX());
        } else {
            rectangle.setTranslateX(0);
        }

        if (height < 0) {
            rectangle.setTranslateY(y - rectangle.getY());
        } else {
            rectangle.setTranslateY(0);
        }

        rectangle.setWidth(Math.abs(width));
        rectangle.setHeight(Math.abs(height));
    }

    @Override
    public void mouseReleased(double x, double y) {
        if (rectangle.getWidth() == 0 || rectangle.getHeight() == 0) {
            rectangle.setVisible(false);
            rectangle.setWidth(0);
            rectangle.setHeight(0);
        }
    }
}
