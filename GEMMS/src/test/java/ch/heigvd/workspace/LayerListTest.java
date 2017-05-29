package ch.heigvd.workspace;

import java.util.List;
import javafxrule.JavaFXThreadingRule;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class LayerListTest {
   // List of nodes for all the series of tests
   private static ObservableList<Node> staticList;
   
   // LayerListcreated for every test
   private LayerList<Node> layerList;
   
   // Rule for running tests in a JavafX Thread
   @org.junit.Rule
   public JavaFXThreadingRule rule = new JavaFXThreadingRule();
   
   public LayerListTest() {
   }
   
   /**
    * Before all tests, we create the content of the list of nodes
    */
   @BeforeClass
   public static void setUpClass() {
      AnchorPane parent = new AnchorPane();
      
      // Add three circles
      for (int i = 0; i < 3; ++i)
         parent.getChildren().add(new Circle(i * 100, i * 100, 20));
      
      // Add three rectangles
      for (int i = 0; i < 3; ++i)
         parent.getChildren().add(new Rectangle(i * 20, i * 20, 40, 20));
      
      
      staticList = parent.getChildren();
   }
   
   @AfterClass
   public static void tearDownClass() {
   }
   
   /**
    * Before each test we create a new LayerList targeting the list of nodes
    */
   @Before
   public void setUp() {
      layerList = new LayerList<>(staticList);
   }
   
   @After
   public void tearDown() {
   }

   /**
    * Test of getSelectedItems method, of class LayerList.
    */
   @Test
   public void testGetSelectedItems() {
   }

   /**
    * Test of clearSelection method, of class LayerList.
    */
   @Test
   public void testClearSelection() {
      // Select items
      for (int i = 0; i < staticList.size(); ++i) {
         layerList.selectLayerByIndex(i);
      }
      // Clear selection
      layerList.clearSelection();
      assertTrue(layerList.getSelectedItems().isEmpty());
   }

   /**
    * Test of selectTopLayer method, of class LayerList.
    */
   @Test
   public void testSelectTopLayer() {
      layerList.selectTopLayer();
      
      // Check that there is only one layer selected, and it is the top layer
      assertTrue(layerList.getSelectedItems().size() == 1);
      assertTrue(layerList.getSelectedItems().get(0) == staticList.get(staticList.size() -1));
   }

   /**
    * Test of selectBottomLayer method, of class LayerList.
    */
   @Test
   public void testSelectBottomLayer() {
      layerList.selectBottomLayer();
      
      // Check that there is only one selected items, and that it is the bottom layer
      assertTrue(layerList.getSelectedItems().size() == 1);
      assertTrue(layerList.getSelectedItems().get(0) == staticList.get(0));
   }

   /**
    * Test of selectLayers method, of class LayerList.
    */
   @Test
   public void testSelectLayers() {
      // Select arbitrary layers
      layerList.selectLayers(staticList.get(5), staticList.get(0), staticList.get(3));
      
      List<Node> list = layerList.getSelectedItems();
      // Ensure there is the right number of selected items
      assertEquals(3, list.size());
      
      // Ensure the right items are selected
      assertTrue(list.contains(staticList.get(0)));
      assertTrue(list.contains(staticList.get(3)));
      assertTrue(list.contains(staticList.get(5)));
      
      // Ensure it doesn't contain the other items
      assertFalse(list.contains(staticList.get(1)));
      assertFalse(list.contains(staticList.get(2)));
      assertFalse(list.contains(staticList.get(4)));
   }

   /**
    * Test of selectLayer method, of class LayerList.
    */
   @Test
   public void testSelectLayer() {
      // Select one item
      layerList.selectLayer(staticList.get(0));
      assertEquals(1, layerList.getSelectedItems().size());
      assertTrue(layerList.getSelectedItems().contains(staticList.get(0)));
      
      // Select another and check everything is right
      layerList.selectLayer(staticList.get(1));
      assertEquals(2, layerList.getSelectedItems().size());
      assertTrue(layerList.getSelectedItems().contains(staticList.get(0)));
      assertTrue(layerList.getSelectedItems().contains(staticList.get(1)));
   }

   /**
    * Test of selectLayerByIndex method, of class LayerList.
    */
   @Test
   public void testSelectLayerByIndex() {
   }
   
}
