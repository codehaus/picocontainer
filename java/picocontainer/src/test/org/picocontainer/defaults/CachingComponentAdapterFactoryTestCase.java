package org.picocontainer.defaults;

import org.picocontainer.tck.AbstractComponentAdapterFactoryTestCase;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.testmodel.Touchable;

/**
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @version $Revision$
 */
public class CachingComponentAdapterFactoryTestCase extends AbstractComponentAdapterFactoryTestCase {
    protected void setUp() throws Exception {
        picoContainer = new DefaultPicoContainer(createComponentAdapterFactory());
    }

    protected ComponentAdapterFactory createComponentAdapterFactory() {
        return new CachingComponentAdapterFactory(new ConstructorComponentAdapterFactory());
    }

    public void testContainerReturnsSameInstaceEachCall() {
        picoContainer.registerComponentImplementation(Touchable.class, SimpleTouchable.class);
        Touchable t1 = (Touchable) picoContainer.getComponentInstance(Touchable.class);
        Touchable t2 = (Touchable) picoContainer.getComponentInstance(Touchable.class);
        assertSame(t1, t2);
    }
}