/**
 * Fichier: SizeConfigurableTool.java
 * Date: 31.05.2017
 *
 * @author Guillaume Milani
 * @author Edward Ransome
 * @author Mathieu Monteverde
 * @author Michael Spierer
 * @author Sathiya Kirushnapillai
 */
package ch.heigvd.tool.settings;

/**
 * <h1>SizeConfigurableTool</h1>
 *
 * SizeConfigurableTools represent tools that can be resized.
 */
public interface SizeConfigurableTool {

    /**
     * Set the size of the object
     *
     * @param size
     */
    public void setSize(int size);

    /**
     * Return the current size of the tool
     *
     * @return
     */
    public int getSize();
}
