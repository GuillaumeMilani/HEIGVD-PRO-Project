package ch.heigvd.tool.settings;

/**
 * Size configurable objects are objects that can resized. 
 * 
 * Typically ToolSizeSettings objects depend on this interface to manage
 * the size of the tool they are targeting.
 * @author mathieu
 */
public interface SizeConfigurableTool {
   /**
    * Set the size of the object
    * @param size 
    */
   public void setSize(int size);
   
   /**
    * Return the size of the object. The ToolSizeSettings use this method 
    * to decide id they should use the tool current size or give it a news one.
    * 
    * This method should return -1 if the tool consider it should be resized, 
    * or their current size if they want to keep there current size.
    * @return 
    */
   public int getSize();
}
