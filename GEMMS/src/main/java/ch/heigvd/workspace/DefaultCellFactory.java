/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.heigvd.workspace;

import javafx.collections.ObservableList;

/**
 * ICellFactory implementation to create DefaultCell objects.
 * @author mathieu
 */
public class DefaultCellFactory<T> implements ICellFactory<T> {

   @Override
   public Cell createCell(T element, ObservableList<T> listElements) {
      return new DefaultCell(element);
   }
   
}

