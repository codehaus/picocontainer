package org.picocontainer.tck;

import junit.framework.TestCase;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoInitializationException;

public abstract class AbstractBasicCompatabilityTestCase extends TestCase {

    protected PicoContainer picoContainer;

    protected void setUp() throws Exception {
        picoContainer = createPicoContainer();
    }

    abstract public PicoContainer createPicoContainer() throws Exception;

    public void testNotNull() {
        assertNotNull("Are you calling super.setUp() in your setUp method?", picoContainer);
    }

    public void testBasicInstantiationAndContainment() throws PicoInitializationException {
        picoContainer.instantiateComponents();
        assertTrue("Container should have Touchable component",
                picoContainer.hasComponent(Touchable.class));
        assertTrue("Container should have DependsOnTouchable component",
                picoContainer.hasComponent(DependsOnTouchable.class));
        assertTrue("Component should be instance of Touchable",
                picoContainer.getComponent(Touchable.class) instanceof Touchable);
        assertTrue("Component should be instance of DependsOnTouchable",
                picoContainer.getComponent(DependsOnTouchable.class) instanceof DependsOnTouchable);
    }


}
