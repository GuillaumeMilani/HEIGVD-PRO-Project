private void writeObject(ObjectOutputStream s) { 
   // ...
   
   // Write the size
   s.writeDouble(width);
   s.writeDouble(height);
   
   // ...
}

private void readObject(ObjectInputStream s) {
   // ...
   
   // Get the size of the canvas
   double width = s.readDouble();
   double height = s.readDouble();
   
   // ...
}
