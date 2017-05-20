
package ch.heigvd.dialog;

import javafx.scene.paint.Color;

public class NewDocument {
    private int width;
    private int height;
    private Color color;

    public NewDocument(int w, int h, Color c) {
        width = w;
        height = h;
        color = c;
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeiht() {
        return height;
    }
    
    public Color getColor() {
        return color;
    }
}
