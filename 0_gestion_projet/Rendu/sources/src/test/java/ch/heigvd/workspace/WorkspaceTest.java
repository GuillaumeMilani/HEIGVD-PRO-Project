package ch.heigvd.workspace;

import ch.heigvd.layer.GEMMSCanvas;
import ch.heigvd.layer.GEMMSText;
import ch.heigvd.tool.Brush;
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
   private Workspace sWorkspace;
   
   @org.junit.Rule
   public JavaFXThreadingRule rule = new JavaFXThreadingRule();
   
   public WorkspaceTest() {
   }
   
   @BeforeClass
   public static void setUpClass() {
   }
   
   @AfterClass
   public static void tearDownClass() {
   }
   
   @Before
   public void setUp() {
      sWorkspace = new Workspace(200, 200);
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
      GEMMSCanvas c = new GEMMSCanvas(200, 200);
      sWorkspace.addLayer(c);
      
      // Check that the workspace contains the new layer
      assertEquals(size + 1, sWorkspace.getLayers().size());
      assertTrue(sWorkspace.getLayers().contains(c));
   }

   /**
    * Test of removeLayer method, of class Workspace.
    */
   @Test
   public void testRemoveLayer() {
      int size = sWorkspace.getLayers().size();
      GEMMSCanvas c = new GEMMSCanvas(200, 200);
      sWorkspace.addLayer(c);
      
      sWorkspace.removeLayer(c);
      assertEquals(size, sWorkspace.getLayers().size());
      assertFalse(sWorkspace.getLayers().contains(c));
   }

   /**
    * Test of getCurrentLayers method, of class Workspace.
    */
   @Test
   public void testGetCurrentLayers() {
      Workspace instance = new Workspace(200, 200);
      GEMMSCanvas r = new GEMMSCanvas(200, 200);
      GEMMSCanvas c = new GEMMSCanvas(200, 200);
      instance.addLayer(c);
      instance.addLayer(r);
      instance.selectLayerByIndex(1);
      
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
      
      GEMMSCanvas r = new GEMMSCanvas(200, 200);
      GEMMSCanvas c = new GEMMSCanvas(200, 200);
      
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
      assertEquals(1.5, sWorkspace.getWorkspaceScaleX(), 0.0001);
      assertEquals(1.5, sWorkspace.getWorkspaceScaleY(), 0.0001);
      
      sWorkspace.zoom(1);
      assertEquals(1.5, sWorkspace.getWorkspaceScaleX(), 0.0001);
      assertEquals(1.5, sWorkspace.getWorkspaceScaleY(), 0.0001);
      
      sWorkspace.zoom(2);
      assertEquals(1.5 * 2, sWorkspace.getWorkspaceScaleX(), 0.0001);
      assertEquals(1.5 * 2, sWorkspace.getWorkspaceScaleY(), 0.0001);
   }

   /**
    * Test of move method, of class Workspace.
    */
   @Test
   public void testMove() {
      double initX = sWorkspace.getLayerTool().getTranslateX();
      double initY = sWorkspace.getLayerTool().getTranslateY();
      
      sWorkspace.move(10, 10);
      assertEquals(initX + 10, sWorkspace.getLayerTool().getTranslateX(), 0.00001);
      assertEquals(initY + 10, sWorkspace.getLayerTool().getTranslateY(), 0.00001);
      
      
      sWorkspace.move(-20, -20);
      assertEquals(initX - 10, sWorkspace.getLayerTool().getTranslateX(), 0.00001);
      assertEquals(initY - 10, sWorkspace.getLayerTool().getTranslateY(), 0.00001);
   }

   /**
    * Test of resizeCanvas method, of class Workspace.
    */
   @Test
   public void testResizeCanvas() {
      sWorkspace.resizeCanvas(100, 100, 0, 0);
      assertEquals(100, (int)sWorkspace.width());
      assertEquals(100, (int)sWorkspace.height());
   }

   /**
    * Test of setCurrentTool method, of class Workspace.
    */
   @Test
   public void testSetCurrentTool() {
      Brush b = new Brush(sWorkspace);
      sWorkspace.setCurrentTool(b);
      assertTrue(b == sWorkspace.getCurrentTool());
   }

   /**
    * Test of getCurrentTool method, of class Workspace.
    */
   @Test
   public void testGetCurrentTool() {
      Brush b = new Brush(sWorkspace);
      sWorkspace.setCurrentTool(b);
      assertTrue(b == sWorkspace.getCurrentTool());
   }

   /**
    * Test of width method, of class Workspace.
    */
   @Test
   public void testWidth() {
      Workspace instance = new Workspace(200, 300);
      assertEquals(200, instance.width());
   }

   /**
    * Test of height method, of class Workspace.
    */
   @Test
   public void testHeight() {
      Workspace instance = new Workspace(200, 300);
      assertEquals(300, instance.height());
   }

   /**
    * Test of selectLayers method, of class Workspace.
    */
   @Test
   public void testSelectLayers() {
      GEMMSCanvas c = new GEMMSCanvas();
      GEMMSText t = new GEMMSText("Bonjour");
      sWorkspace.getLayers().add(c);
      sWorkspace.getLayers().add(t);
      sWorkspace.selectLayers(c, t);
      
      assertTrue(sWorkspace.getCurrentLayers().contains(c));
      assertTrue(sWorkspace.getCurrentLayers().contains(t));
   }

   /**
    * Test of selectLayer method, of class Workspace.
    */
   @Test
   public void testSelectLayer() {
      GEMMSCanvas c = new GEMMSCanvas();
      GEMMSText t = new GEMMSText("Bonjour");
      sWorkspace.getLayers().add(c);
      sWorkspace.getLayers().add(t);
      
      sWorkspace.selectLayer(c);
      
      assertTrue(sWorkspace.getCurrentLayers().contains(c));
   }

   /**
    * Test of selectLayerByIndex method, of class Workspace.
    */
   @Test
   public void testSelectLayerByIndex() {
      GEMMSCanvas c = new GEMMSCanvas();
      GEMMSText t = new GEMMSText("Bonjour");
      sWorkspace.getLayers().add(c);
      sWorkspace.getLayers().add(t);
      
      sWorkspace.selectLayerByIndex(1);
      
      assertTrue(sWorkspace.getCurrentLayers().contains(t));
   }
   
}
