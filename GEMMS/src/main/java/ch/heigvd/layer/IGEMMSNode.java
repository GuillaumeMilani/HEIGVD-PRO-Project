package ch.heigvd.layer;

import ch.heigvd.tool.Rotate;
import javafx.scene.transform.Translate;

import java.io.Serializable;

/**
 * Created by lognaume on 5/16/17.
 */
public interface IGEMMSNode extends Serializable {
    javafx.scene.transform.Rotate rotateX = new javafx.scene.transform.Rotate();
    javafx.scene.transform.Rotate rotateY = new javafx.scene.transform.Rotate();
    javafx.scene.transform.Rotate rotateZ = new javafx.scene.transform.Rotate();
    Translate t  = new Translate();

}
