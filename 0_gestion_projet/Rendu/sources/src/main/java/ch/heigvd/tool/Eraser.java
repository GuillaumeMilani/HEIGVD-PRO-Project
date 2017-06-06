/**
 * Fichier: Eraser.java
 * Date: 31.05.2017
 *
 * @author Guillaume Milani
 * @author Edward Ransome
 * @author Mathieu Monteverde
 * @author Michael Spierer
 * @author Sathiya Kirushnapillai
 */
package ch.heigvd.tool;

import ch.heigvd.tool.settings.SizeConfigurableTool;
import ch.heigvd.workspace.Workspace;
import javafx.scene.canvas.GraphicsContext;

/**
 * <h1>Eraser</h1>
 * The Eraser clear JavaFx Canvas pixels content.
 */
public class Eraser extends LineTool implements SizeConfigurableTool {

    /**
     * Constructor
     *
     * @param workspace the Workspace to work on
     */
    public Eraser(Workspace workspace) {
        this(workspace, 5);
    }

    /**
     * Constructor
     *
     * @param workspace the workspace to work on
     * @param size the size of the Eraser
     */
    public Eraser(Workspace workspace, int size) {
        super(workspace, size);
    }

    /**
     * Clear the areav around the given pixel position
     *
     * @param x the x coordinate of the pixel
     * @param y the y coordinate of the pixel
     * @param gc the GraphicsContext of the canvas
     */
    @Override
    public void drawPixel(int x, int y, GraphicsContext gc) {
        gc.clearRect(x - (size / 2.0), y - (size / 2.0), size, size);
    }
}
