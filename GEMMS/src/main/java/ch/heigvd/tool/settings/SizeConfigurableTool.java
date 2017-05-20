package ch.heigvd.tool.settings;

/**
 * SizeConfigurableTools represent tools that can be resized. 
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
