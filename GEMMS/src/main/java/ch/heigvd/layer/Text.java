package javafxtest;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

public class Text extends javafx.scene.text.Text implements Serializable {
	
	/**
	 * Constructor
	 */
	public Text() {
		super();
	}
	
	/**
	 * Constructor
	 * 
	 * @param text text to be contained in the instance
	 */
	public Text(String text) {
		super(text);
	}
	
	/**
	 * Constructor
	 * 
	 * @param x the horizontal position of the text
	 * @param y the vertical position of the text
	 * @param text text to be contained in the instance
	 */
	public Text(double x, double y, String text) {
		super(x, y, text);
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
    }
    
    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
    	// Set the test
    	setText((String)s.readObject());

		// Set the position
    	setX(s.readDouble());
    	setY(s.readDouble());
    	
    	// Set the font
    	setFont(Font.font((String)s.readObject(), s.readDouble()));
    
    	// Set fill info
    	setFill(Paint.valueOf((String)s.readObject()));
    }

}
