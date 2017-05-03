/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.heigvd.workspace;

/**
 *
 * @author mathieu
 */

/**
 * This interface reprsents objects that can be listed inside the workspace
 * Listview.
 */
public interface LayerListable {
   /**
    * This method should return the name of the layer that will be displayed in 
    * the ListView.
    * @return The name of the layer
    */
   public String getLayerName();
   
   /**
    * This method should return the CSS class to apply to the thumbnail in the
    * ListView. Typically it could be a class that adds an icon to the thumbnail
    * or that changes its background color.
    * @return the class name as a String.
    */
   public String getThumbnailClass();
}
