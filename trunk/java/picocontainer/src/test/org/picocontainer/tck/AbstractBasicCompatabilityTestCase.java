package org.picocontainer.tck;

import junit.framework.TestCase;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoInitializationException;
import org.nanocontainer.testmodel.FredImpl;
import org.nanocontainer.testmodel.Wilma;

public abstract class AbstractBasicCompatabilityTestCase extends TestCase {

    protected PicoContainer picoContainer;

    public void testNotNull() {
        assertNotNull("You need to assign 'picoContainer' in your setup() method", picoContainer);
    }

    public void testBasicInstantiationAndContainment() throws PicoInitializationException {        
        picoContainer.instantiateComponents();
        assertTrue(picoContainer.hasComponent(Wilma.class));
        assertTrue(picoContainer.hasComponent(FredImpl.class));
        assertTrue(picoContainer.getComponent(Wilma.class) instanceof Wilma);
        assertTrue(picoContainer.getComponent(FredImpl.class) instanceof FredImpl);
    }


}
