package picocontainer.aggregated;

import junit.framework.TestCase;
import picocontainer.testmodel.WilmaImpl;
import picocontainer.ClassRegistrationPicoContainer;
import picocontainer.PicoRegistrationException;
import picocontainer.PicoStopException;
import picocontainer.PicoContainer;
import picocontainer.hierarchical.HierarchicalPicoContainer;

public class AggregatedContainersContainerTestCase extends TestCase {
    private ClassRegistrationPicoContainer pico;
    private AggregatedContainersContainer.Filter filter;

    public void setUp() throws PicoRegistrationException {
        pico = new HierarchicalPicoContainer.Default();
        pico.registerComponent(WilmaImpl.class);
        filter = new AggregatedContainersContainer.Filter(pico);
    }

    public void tearDown() throws PicoStopException {
        try {
            pico.stop();
        } catch (IllegalStateException e) {
        }
        pico = null;
        filter = null;
    }

    public void testGetComponents() {
        assertEquals("Content of Component arrays should be the same", pico, filter.getSubject());
    }

    public void testGetComponentTypes() {
        assertEquals("Content of Component type arrays should be the same", pico, filter.getSubject());
    }

    public void testGetComponent() {
        assertSame("Wilma should be the same", pico.getComponent(WilmaImpl.class), filter.getComponent(WilmaImpl.class));
    }

    public void testHasComponent() {
        assertEquals("Containers should contain the same", pico.hasComponent(WilmaImpl.class), filter.hasComponent(WilmaImpl.class));
    }

    public void testNullContainer() {
        try {
            AggregatedContainersContainer.Filter badOne = new AggregatedContainersContainer.Filter(null);
            fail("Should have failed with an NPE");
        } catch (NullPointerException e) {
            // fine
        }
    }

    public void testNullArrayContainer() {
        try {
            AggregatedContainersContainer badOne = new AggregatedContainersContainer(null);
            fail("Should have failed with an NPE");
        } catch (NullPointerException e) {
            // fine
        }
    }

    public void testGetToFilterFor() {
        assertSame("The PicoContainer to filter for should be the one made in setUp", pico, filter.getSubject());
    }

    public void testBasic() {

        final String acomp = "hello";
        final Integer bcomp = new Integer(123);

        PicoContainer a = new PicoContainer() {
            public boolean hasComponent(Class compType) {
                return compType == String.class;
            }

            public Object getComponent(Class compType) {
                return compType == String.class ? acomp : null;
            }

            public Object[] getComponents() {
                return new Object[] {acomp};
            }

            public Class[] getComponentTypes() {
                return new Class[] {String.class};
            }
        };

        PicoContainer b = new PicoContainer() {
            public boolean hasComponent(Class compType) {
                return compType == Integer.class;
            }

            public Object getComponent(Class compType) {
                return compType == Integer.class ? bcomp : null;
            }

            public Object[] getComponents() {
                return new Object[] {bcomp};
            }

            public Class[] getComponentTypes() {
                return new Class[] {Integer.class};
            }
        };

        AggregatedContainersContainer acc = new AggregatedContainersContainer(new PicoContainer[] {a, b});

        assertTrue(acc.hasComponent(String.class));
        assertTrue(acc.hasComponent(Integer.class));
        assertTrue(acc.getComponent(String.class) == acomp);
        assertTrue(acc.getComponent(Integer.class) == bcomp);
        assertTrue(acc.getComponents().length == 2);

    }

    public void testEmpty() {

        AggregatedContainersContainer acc = new AggregatedContainersContainer(new PicoContainer[0]);
        assertTrue(acc.hasComponent(String.class) == false);
        assertTrue(acc.getComponent(String.class) == null);
        assertTrue(acc.getComponents().length == 0);

    }
}
