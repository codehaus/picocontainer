package org.picocontainer.tck;

import junit.framework.TestCase;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.testmodel.Touchable;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.defaults.*;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public abstract class AbstractComponentAdapterFactoryTestCase extends TestCase {
    protected DefaultPicoContainer picoContainer;

    protected abstract ComponentAdapterFactory createComponentAdapterFactory();

    protected void setUp() throws Exception {
		picoContainer = new DefaultPicoContainer();
	}

    public void testRegisterComponent() throws PicoRegistrationException, AssignabilityRegistrationException {
		ComponentAdapter componentAdapter =
                createComponentAdapterFactory().createComponentAdapter(Touchable.class, SimpleTouchable.class, null);

		picoContainer.registerComponent(componentAdapter);

		assertTrue(picoContainer.getComponentAdapters().contains(componentAdapter));
	}

    public void testUnregisterComponent() throws PicoRegistrationException, AssignabilityRegistrationException {
        ComponentAdapter componentAdapter =
                createComponentAdapterFactory().createComponentAdapter(Touchable.class, SimpleTouchable.class, null);

		picoContainer.registerComponent(componentAdapter);
		picoContainer.unregisterComponent(Touchable.class);

		assertFalse(picoContainer.getComponentAdapters().contains(componentAdapter));
	}
}
