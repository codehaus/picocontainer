package picocontainer;

import junit.framework.TestCase;
import picocontainer.testmodel.WilmaImpl;

/**
 * 
 * @author Aslak Hellesoy
 * @version $Revision: 0 $
 */
public class FilterContainerTestCase extends TestCase {
    private PicoContainer pico;
    private FilterContainer filter;

    public void setUp() throws PicoRegistrationException {
        pico = new HierarchicalPicoContainer.Default();
        pico.registerComponent(WilmaImpl.class);
        filter = new FilterContainer(pico);
    }

    public void tearDown() throws PicoStopException {
        pico.stop();
        pico = null;
        filter = null;
    }

    public void testGetComponents() {
        assertSame("Components arrays should be the same", pico.getComponents(), filter.getComponents());
        assertEquals("Content of Component arrays should be the same", pico, filter.getToFilterFor());
    }

    public void testGetComponentTypes() {
        assertSame("Component type arrays should be the same", pico.getComponentTypes(), filter.getComponentTypes());
        assertEquals("Content of Component type arrays should be the same", pico, filter.getToFilterFor());
    }

    public void testGetComponent() {
        assertSame("Wilma should be the same", pico.getComponent(WilmaImpl.class), filter.getComponent(WilmaImpl.class));
    }

    public void testHasComponent() {
        assertEquals("Containers should contain the same", pico.hasComponent(WilmaImpl.class), filter.hasComponent(WilmaImpl.class));
    }

    public void testNullContainer() {
        try {
            FilterContainer badOne = new FilterContainer(null);
            fail("Should have failed with an NPE");
        } catch (NullPointerException e) {
            // fine
        }
    }

    public void testGetToFilterFor() {
        assertSame("The Container to filter for should be the one made in setUp", pico, filter.getToFilterFor());
    }

}
