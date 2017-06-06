/**
 * Fichier: HistoryNotifier.java
 * Date: 31.05.2017
 *
 * @author Guillaume Milani
 * @author Edward Ransome
 * @author Mathieu Monteverde
 * @author Michael Spierer
 * @author Sathiya Kirushnapillai
 */
package ch.heigvd.workspace;

import java.util.Observable;

public class HistoryNotifier extends Observable {

    public void notifyHistory() {
        setChanged();
        notifyObservers();
    }
}
