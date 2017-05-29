package ch.heigvd.workspace;

import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafxrule.JavaFXThreadingRule;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author mathieu
 */
public class WorkspaceTest {
   
   // Workspace to last for all the tests
   private static Workspace sWorkspace;
   
   @org.junit.Rule
   public JavaFXThreadingRule rule = new JavaFXThreadingRule();
   
   public WorkspaceTest() {
   }
   
   @BeforeClass
   public static void setUpClass() {
      //sWorkspace = new Workspace(200, 200);
   }
   
   @AfterClass
   public static void tearDownClass() {
   }
   
   @Before
   public void setUp() {
   }
   
   @After
   public void tearDown() {
   }

   /**
    * Test of addLayer method, of class Workspace.
    */
   @Test
   public void testAddLayer() {
      int size = sWorkspace.getLayers().size();
      Rectangle r = new Rectangle();
      sWorkspace.addLayer(r);
      
      // Check that the workspace contains the new layer
      assertEquals(size + 1, sWorkspace.getLayers().size());
      assertTrue(sWorkspace.getLayers().contains(r));
   }

   /**
    * Test of removeLayer method, of class Workspace.
    */
   @Test
   public void testRemoveLayer() {
      int size = sWorkspace.getLayers().size();
      Rectangle r = new Rectangle();
      sWorkspace.addLayer(r);
      
      sWorkspace.removeLayer(r);
      assertEquals(size, sWorkspace.getLayers().size());
      assertFalse(sWorkspace.getLayers().contains(r));
   }

   /**
    * Test of getCurrentLayers method, of class Workspace.
    */
   @Test
   public void testGetCurrentLayers() {
      Workspace instance = new Workspace(200, 200);
      Rectangle r = new Rectangle();
      Circle c = new Circle();
      instance.addLayer(r);
      instance.addLayer(c);
      instance.selectLayerByIndex(0);
      
      // Check that it returns contained layers
      assertTrue(instance.getCurrentLayers().contains(r));
      assertFalse(instance.getCurrentLayers().contains(c));
   }

   /**
    * Test of getLayers method, of class Workspace.
    */
   @Test
   public void testGetLayers() {
      Workspace instance = new Workspace(200, 200);
      assertTrue(instance.getLayers().isEmpty());
      
      Rectangle r = new Rectangle();
      Circle c = new Circle();
      
      instance.addLayer(r);
      instance.addLayer(c);
      
      assertEquals(2, instance.getLayers().size());
      assertTrue(instance.getLayers().contains(r));
      assertTrue(instance.getLayers().contains(c));
   }

   /**
    * Test of zoom method, of class Workspace.
    */
   @Test
   public void testZoom() {
      sWorkspace.zoom(1.5);
      assertEquals(1.5, sWorkspace.getScaleX());
      assertEquals(1.5, sWorkspace.getScaleY());
      
      sWorkspace.zoom(1);
      assertEquals(1.5, sWorkspace.getScaleX());
      assertEquals(1.5, sWorkspace.getScaleY());
      
      sWorkspace.zoom(2);
      assertEquals(1.5 * 2, sWorkspace.getScaleX());
      assertEquals(1.5 * 2, sWorkspace.getScaleY());
   }

   /**
    * Test of move method, of class Workspace.
    */
   @Test
   public void testMove() {
   }

   /**
    * Test of resizeCanvas method, of class Workspace.
    */
   @Test
   public void testResizeCanvas() {
   }

   /**
    * Test of getWorkspaceController method, of class Workspace.
    */
   @Test
   public void testGetWorkspaceController() {
   }

   /**
    * Test of setCurrentTool method, of class Workspace.
    */
   @Test
   public void testSetCurrentTool() {
   }

   /**
    * Test of getCurrentTool method, of class Workspace.
    */
   @Test
   public void testGetCurrentTool() {
   }

   /**
    * Test of width method, of class Workspace.
    */
   @Test
   public void testWidth() {
   }

   /**
    * Test of height method, of class Workspace.
    */
   @Test
   public void testHeight() {
   }

   /**
    * Test of getWorkspaceScaleX method, of class Workspace.
    */
   @Test
   public void testGetWorkspaceScaleX() {
   }

   /**
    * Test of getWorkspaceScaleY method, of class Workspace.
    */
   @Test
   public void testGetWorkspaceScaleY() {
   }

   /**
    * Test of getLayerTool method, of class Workspace.
    */
   @Test
   public void testGetLayerTool() {
   }

   /**
    * Test of selectLayers method, of class Workspace.
    */
   @Test
   public void testSelectLayers() {
   }

   /**
    * Test of selectLayer method, of class Workspace.
    */
   @Test
   public void testSelectLayer() {
   }

   /**
    * Test of selectLayerByIndex method, of class Workspace.
    */
   @Test
   public void testSelectLayerByIndex() {
   }

   /**
    * Test of getHistory method, of class Workspace.
    */
   @Test
   public void testGetHistory() {
   }

   /**
    * Test of notifyHistory method, of class Workspace.
    */
   @Test
   public void testNotifyHistory() {
   }
   
}
