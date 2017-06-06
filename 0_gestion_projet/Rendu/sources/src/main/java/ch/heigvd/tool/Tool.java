/**
 * Fichier: Tool.java
 * Date: 31.05.2017
 *
 * @author Guillaume Milani
 * @author Edward Ransome
 * @author Mathieu Monteverde
 * @author Michael Spierer
 * @author Sathiya Kirushnapillai
 */
package ch.heigvd.tool;

public interface Tool {

    void mousePressed(double x, double y);

    void mouseDragged(double x, double y);

    void mouseReleased(double x, double y);

    void mouseMoved(double x, double y);
}
