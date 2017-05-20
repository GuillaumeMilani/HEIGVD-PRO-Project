package ch.heigvd.layer;

import ch.heigvd.gemms.CSSIcons;
import ch.heigvd.workspace.LayerListable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javafx.geometry.VPos;

import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

public class GEMMSText extends javafx.scene.text.Text implements IGEMMSNode, LayerListable {

   public static final int DEFAULT_SIZE = 12;


   /**
    * Constructor
    */
   public GEMMSText() {
      super();
   }

   /**
    * Constructor
    *
    * @param text text to be contained in the instance
    */
   public GEMMSText(String text) {
      this(0, 0, text);
   }

   /**
    * Constructor
    *
    * @param x the horizontal position of the text
    * @param y the vertical position of the text
    * @param text text to be contained in the instance
    */
   public GEMMSText(double x, double y, String text) {
      super(x, y, text);
      setTextOrigin(VPos.CENTER);
      setTextAlignment(TextAlignment.CENTER);
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();

        // Write the text
        s.writeObject(getText());

        // Write the position
        s.writeDouble(getX());
        s.writeDouble(getY());

        // Write font
        s.writeObject(getFont().getFamily());
        s.writeDouble(getFont().getSize());

        // Write fill info
        s.writeObject(getFill().toString());
        
        // Write translate info
        s.writeDouble(getTranslateX());
        s.writeDouble(getTranslateY());
        s.writeDouble(getTranslateZ());
}

   @Override
   public String getLayerName() {
      String[] parts = getText().split(System.lineSeparator());
      return parts.length >= 0 ? parts[0] : "";
   }

   @Override
   public String getThumbnailClass() {
      return CSSIcons.TEXT;
   }

  private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        // Set the test
        setText((String) s.readObject());

        // Set the position
        setX(s.readDouble());
        setY(s.readDouble());

        // Set the font
        setFont(Font.font((String) s.readObject(), s.readDouble()));

        // Set fill info
        setFill(Paint.valueOf((String) s.readObject()));
        
        // Set translate info
        setTranslateX(s.readDouble());
        setTranslateY(s.readDouble());
        setTranslateZ(s.readDouble());
}

}
