public class Workspace extends StackPane implements Serializable {

    // Workspace that displays layers
    private AnchorPane workspace;

    // LayerList to manage layers
    private LayerList<Node> layerList;
    
    public Workspace(int width, int height) {
        /* ... */

        // Create the LayerList to manage the drawing area children
        layerList = new LayerList<>(workspace.getChildren());

        /* ... */
    }

    /* ... */

    /**
     * Return selected layers
     */
    public List<Node> getCurrentLayers() {
        // Let the LayerList handle the request
        return layerList.getSelectedItems();
    }

    /* ... */
}
