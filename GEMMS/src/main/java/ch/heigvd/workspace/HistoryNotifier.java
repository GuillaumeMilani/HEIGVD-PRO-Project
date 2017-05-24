package ch.heigvd.workspace;

import java.util.Observable;

/**
 * Created by lognaume on 5/23/17.
 */
public class HistoryNotifier extends Observable {
        public void notifyHistory() {
        setChanged();
        notifyObservers();
    }
}
