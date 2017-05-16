package ch.heigvd.tool;

import ch.heigvd.workspace.Workspace;
import static java.lang.System.gc;
import java.util.List;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

/**
 * The Brush tool class. Brush objects are tools that draw lines on a JavafX
 * Canvas object. It is possible to set the color and the width of the brush.
 *
 * @author mathieu
 */
public class Brush implements Tool {

   // The workspace on which this brush is currently working
   Workspace workspace;

   // Color of the brush
   Color color;

   // Size of the brush in pixels
   int size;

   // Keep track of past x, y coordinates
   private double x;
   private double y;

   /**
    * Constructor. Sets the default usage values which are a the color black and
    * a size of 1px.
    *
    * @param workspace the Workspace to work on
    */
   public Brush(Workspace workspace) {
      this.workspace = workspace;
      this.color = Color.BLACK;
      this.size = 10;
   }

   /**
    * Constructor. Specifies the color and size.
    *
    * @param workspace the Workspace to work on
    * @param color the color of the brush
    * @param size the size [px]
    */
   public Brush(Workspace workspace, Color color, int size) {
      this(workspace);
      this.color = color;
      this.size = size;
   }

   /**
    * Method to call on the start of the dragging movement, when the mouse is
    * pressed for the first time.
    *
    * @param x the x coordinate of the event.
    * @param y the y coordinate of the event.
    */
   @Override
   public void mousePressed(double x, double y) {
      this.x = x;
      this.y = y;

      // Get the selected layers of the workspace
      List<Node> layers = workspace.getCurrentLayers();

      // For each node, draw on it
      for (Node node : layers) {
         if (Canvas.class.isInstance(node)) {
            Canvas canvas = (Canvas) node;
            GraphicsContext gc = canvas.getGraphicsContext2D();

            gc.setFill(color);

            int circleSize = 2 * (int) Math.sqrt(2 * (size / 2.0 * size / 2.0));
            gc.fillOval(this.x - circleSize / 2.0, this.y - circleSize / 2.0, circleSize, circleSize);
         }

      }
   }

   /**
    * Method to call during the drag movement of the mouse event (after calling
    * mosuePressed once).
    *
    * @param x the x coordinate of the event
    * @param y the y coordinate of the event
    */
   @Override
   public void mouseDragged(double x, double y) {

      // Get the selected layers of the workspace
      List<Node> layers = workspace.getCurrentLayers();

      // For each node, draw on it
      for (Node node : layers) {
         if (Canvas.class.isInstance(node)) {
            Canvas canvas = (Canvas) node;
            PixelWriter pw = canvas.getGraphicsContext2D().getPixelWriter();

            //gc.setStroke(color);
            //gc.setLineWidth(size);
            drawCustomLine(pw, (int) this.x, (int) this.y, (int) x, (int) y);

         }

      }

      // Update the position
      this.x = x;
      this.y = y;
   }

   private void drawCustomLine(PixelWriter pw, int x1, int y1, int x2, int y2) {
      int dx, dy; // deltas

      dx = x2 - x1;
      if (dx != 0) {
         if (dx > 0) {
            dy = y2 - y1;
            if (dy != 0) {
               if (dy > 0) {
                  if (dx >= dy) {
                     int e = dx;
                     dx = e * 2;
                     dy = dy * 2;
                     while (true) {
                        pw.setColor(x1, y1, color);
                        if (++x1 == x2) {
                           break;
                        }
                        e = e - dy;
                        if (e < 0) {
                           ++y1;
                           e = e + dx;
                        } // end if
                     } // end while
                  } else {
                     int e = dy;
                     dy = e * 2;
                     dx = dx * 2;

                     while (true) {
                        pw.setColor(x1, y1, color);
                        if (++y1 == y2) {
                           break;
                        }
                        e = e - dx;
                        if (e < 0) {
                           ++x1;
                           e = e + dy;
                        } // end if
                     } // end while
                  } // end if
               } else {
                  if (dx >= -dy) {
                     int e = dx;
                     dx = e * 2;
                     dy = dy * 2;
                     while (true) {
                        pw.setColor(x1, y1, color);
                        if (++x1 == x2) {
                           break;
                        }
                        e = e + dy;
                        if (e < 0) {
                           --y1;
                           e = e + dx;
                        } // end if
                     } // end while
                  } else {
                     int e = dy;
                     dy = e * 2;
                     dx = dx * 2;
                     while (true) {
                        pw.setColor(x1, y1, color);
                        --y1;
                        if (y1 == y2) {
                           break;
                        }
                        e = e + dx;
                        if (e > 0) {
                           ++x1;
                           e = e + dy;
                        } // end if
                     } // end while
                  } // end if
               } // end if
            } else {
               do {
                  pw.setColor(x1, y1, color);
               } while (++x1 != x2);
            } // end if
         } else {
            dy = y2 - y1;
            if (dy != 0) {
               if (dy > 0) {
                  if (-dx >= dy) {
                     int e = dx;
                     dx = e * 2;
                     dy = dy * 2;
                     while (true) {
                        pw.setColor(x1, y1, color);
                        if (--x1 == x2) {
                           break;
                        }
                        e = e + dy;
                        if (e >= 0) {
                           ++y1;
                           e = e + dx;
                        } // end if
                     } // end while
                  } else {
                     int e = dy;
                     dy = e * 2;
                     dx = dx * 2;
                     while (true) {
                        pw.setColor(x1, y1, color);
                        ++y1;
                        if (y1 == y2) {
                           break;
                        }
                        e = e + dx;
                        if (e <= 0) {
                           --x1;
                           e = e + dy;
                        } // end if
                     } // end while
                  } // end if
               } else {
                  if (dx <= dy) {
                     int e = dx;
                     dx = e * 2;
                     dy = dy * 2;
                     while (true) {
                        pw.setColor(x1, y1, color);
                        if (--x1 == x2) {
                           break;
                        }
                        e = e - dy;
                        if (e >= 0) {
                           --y1;
                           e = e + dx;
                        } // end if
                     } // end while
                  } else {
                     int e = dy;
                     dy = e * 2;
                     dx = dx * 2;
                     while (true) {
                        pw.setColor(x1, y1, color);
                        --y1;
                        if (y1 == y2) {
                           break;
                        }
                        e = e - dx;
                        if (e >= 0) {
                           --x1;
                           e = e + dy;
                        } // end if
                     } // end while
                  } // end if
               } // end if
            } else {
               do {
                  pw.setColor(x1, y1, color);
               } while (--x1 != x2);
            } // end if
         } // end if
      } else {
         dy = y2 - y1;
         if (dy != 0) {
            if (dy > 0) {
               do {
                  pw.setColor(x1, y1, color);
               } while (++y1 != y2);
            } else {
               do {
                  pw.setColor(x1, y1, color);
               } while (--y1 != y2);
            }
         }
      }
   }

/**
 * Method to call at the end of the drag movement.
 *
 * @param x the x coordinate of the event
 * @param y the y coordinate of the event
 */
@Override
        public void mouseReleased(double x, double y) {

   }

   /**
    * Set the color of the brush.
    *
    * @param color the new Color
    */
   public void setColor(Color color) {
      this.color = color;
   }

   /**
    * Get the current brush color.
    *
    * @return the color of the brush
    */
   public Color getColor() {
      return color;
   }

   /**
    * Set the new size of the brush
    *
    * @param size the new size
    */
   public void setSize(int size) {
      this.size = size;
   }

   /**
    * Get the size of the brush in pixels
    *
    * @return the size of the brush
    */
   public int getSize() {
      return size;
   }

}
