package ch.heigvd.layer;

import javafx.scene.Node;

import java.io.Serializable;

/**
 * Created by lognaume on 5/16/17.
 */
public interface IGEMMSNode extends Serializable, Cloneable {
    public IGEMMSNode clone();
}
