package ch.heigvd.tool.settings;

/**
 * Size configurable objects are objects that can resized. 
 * @author mathieu
 */
public interface SizeConfigurableTool {
   /**
    * Set the size of the object
    * @param size 
    */
   public void setSize(int size);
   
   /**
    * Return the current size of the tool
    * @return 
    */
   public int getSize();
}
