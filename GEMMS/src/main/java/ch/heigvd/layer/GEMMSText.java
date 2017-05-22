package ch.heigvd.layer;

import ch.heigvd.gemms.CSSIcons;
import ch.heigvd.workspace.LayerListable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javafx.geometry.Point3D;
import javafx.geometry.VPos;

import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;


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
     * @param x    the horizontal position of the text
     * @param y    the vertical position of the text
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

        // Write scale info
        s.writeDouble(getScaleX());
        s.writeDouble(getScaleY());
        s.writeDouble(getScaleZ());

        // Wrtie rotate info
        s.writeDouble(getRotate());
        s.writeDouble(getRotationAxis().getX());
        s.writeDouble(getRotationAxis().getY());
        s.writeDouble(getRotationAxis().getZ());

        //Write Transformation
        s.writeInt(getTransforms().size()); // size
        for (Transform t : getTransforms()) {

            if (t instanceof javafx.scene.transform.Rotate) {
                s.writeObject(t.getClass().getSimpleName());
                Rotate rotate = (Rotate) t;
                s.writeDouble(rotate.getAngle());
                s.writeDouble(rotate.getPivotX());
                s.writeDouble(rotate.getPivotY());
                s.writeDouble(rotate.getPivotZ());
                s.writeDouble(rotate.getAxis().getX());
                s.writeDouble(rotate.getAxis().getY());
                s.writeDouble(rotate.getAxis().getZ());
            } else {
                s.writeObject("None");
            }
        }
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

        // Set scale info
        setScaleX(s.readDouble());
        setScaleY(s.readDouble());
        setScaleZ(s.readDouble());

        // Set rotate info
        setRotate(s.readDouble());
        setRotationAxis(new Point3D(s.readDouble(), s.readDouble(), s.readDouble()));


        //Set Transformation
        int sizeTransformation = s.readInt();
        for (int i = 0; i < sizeTransformation; i++) {
            String classOfTransformation = (String) s.readObject();
            switch (classOfTransformation) {
                case "Rotate":
                    double angle = s.readDouble();
                    double pivotX = s.readDouble();
                    double pivotY = s.readDouble();
                    double pivotZ = s.readDouble();
                    double pAxisX = s.readDouble();
                    double pAxisY = s.readDouble();
                    double pAxisZ = s.readDouble();
                    Point3D axis = new Point3D(pAxisX, pAxisY, pAxisZ);
                    getTransforms().add(new Rotate(angle, pivotX, pivotY, pivotZ, axis));
                    break;
                default:
                    System.out.println("Serialisation erreur");
                    break;
            }
        }

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

}
