package ch.heigvd.tool;

import ch.heigvd.workspace.History;
import ch.heigvd.workspace.Workspace;
import com.sun.xml.internal.ws.api.pipe.FiberContextSwitchInterceptor;

import java.util.Observable;

/**
 * Created by lognaume on 5/19/17.
 */
public abstract class AbstractTool extends Observable implements Tool {
    protected Workspace workspace;

    public AbstractTool(Workspace workspace) {
        this.workspace = workspace;
        addObserver(workspace.getHistory());
    }

    @Override
    public abstract void mousePressed(double x, double y);

    @Override
    public abstract void mouseDragged(double x, double y);

    @Override
    public abstract void mouseReleased(double x, double y);
}
