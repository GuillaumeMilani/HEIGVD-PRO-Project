package ch.heigvd.workspace;

import javafx.geometry.Pos;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

/**
 * Default Cell implementation for the GEMMS application.
 * @author mathieu
 */
public class DefaultCell<T> extends Cell<T> {

   static int count = 0;

   public DefaultCell(T target) {
      super(target);
      setStyle("-fx-background-color: #ededed");

      // Create a thumbnail for estetic purposes
      AnchorPane rect = new AnchorPane();
      rect.setPrefSize(40, 40);

      // Add a Label (To be replaced by the name of the type of Node ?)
      Text t = new Text("Layer");

      if (LayerListable.class.isInstance(target)) {
         rect.getStyleClass().add(((LayerListable) target).getThumbnailClass());
         t.setText(((LayerListable) target).getLayerName());
      }

      // Add it to the LayerCell
      getChildren().add(rect);
      getChildren().add(t);

      // Align everything
      setAlignment(Pos.CENTER_LEFT);
      setSpacing(10);
   }

   @Override
   public void select() {
      super.select();
      setStyle("-fx-background-color: #aaa");
   }

   @Override
   public void deSelect() {
      super.deSelect();
      setStyle("-fx-background-color: #ededed");
   }
}
