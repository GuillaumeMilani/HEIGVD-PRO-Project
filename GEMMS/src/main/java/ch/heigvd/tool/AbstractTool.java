package ch.heigvd.tool;

import ch.heigvd.workspace.HistoryNotifier;
import ch.heigvd.workspace.Workspace;


public abstract class AbstractTool implements Tool {
    protected final Workspace workspace;
    protected HistoryNotifier notifier;

    public AbstractTool(Workspace workspace) {
        this.workspace = workspace;
        notifier = new HistoryNotifier();
        notifier.addObserver(workspace.getHistory());
    }

    @Override
    public abstract void mousePressed(double x, double y);

    @Override
    public abstract void mouseDragged(double x, double y);

    @Override
    public abstract void mouseReleased(double x, double y);
}
