/**
 * Fichier: ToolSettings.java
 * Date: 31.05.2017
 *
 * @author Guillaume Milani
 * @author Edward Ransome
 * @author Mathieu Monteverde
 * @author Michael Spierer
 * @author Sathiya Kirushnapillai
 */
package ch.heigvd.tool.settings;

import javafx.scene.layout.HBox;

/**
 * <h1>ToolSettings</h1>
 *
 * ToolSettings is a general abstraction for a component which purpose is to manage a
 * specific tool instance settings.
 *
 * It extends HBox in order to be used as a block in an other containing Node.
 */
public abstract class ToolSettings extends HBox {

    // Set a general spaceing of 10 pixels.
    public ToolSettings() {
        setSpacing(10);
    }
}
