package org.picocontainer.tck;

import junit.framework.TestCase;
import org.picocontainer.defaults.DefaultComponentRegistry;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.testmodel.Touchable;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.internals.ComponentAdapter;
import org.picocontainer.internals.ComponentRegistry;
import org.picocontainer.internals.ComponentAdapterFactory;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public abstract class AbstractComponentAdapterFactoryTestCase extends TestCase {
    protected ComponentRegistry componentRegistry;

    protected abstract ComponentAdapterFactory createComponentAdapterFactory();

    protected void setUp() throws Exception {
		componentRegistry = new DefaultComponentRegistry();
	}

    public void testRegisterComponent() throws PicoIntrospectionException {
		ComponentAdapter componentAdapter =
                createComponentAdapterFactory().createComponentAdapter(Touchable.class, SimpleTouchable.class, null);

		componentRegistry.registerComponent(componentAdapter);

		assertTrue(componentRegistry.getComponentAdapters().contains(componentAdapter));
	}

    public void testUnregisterComponent() throws PicoIntrospectionException {
        ComponentAdapter componentAdapter =
                createComponentAdapterFactory().createComponentAdapter(Touchable.class, SimpleTouchable.class, null);

		componentRegistry.registerComponent(componentAdapter);
		componentRegistry.unregisterComponent(Touchable.class);

		assertFalse(componentRegistry.getComponentAdapters().contains(componentAdapter));
	}
}
