package org.nanocontainer.script.groovy;

import org.nanocontainer.DefaultNanoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.adapters.CachingComponentAdapterFactory;
import org.picocontainer.adapters.AnyInjectionComponentAdapterFactory;
import org.picocontainer.defaults.ComponentFactory;
import org.picocontainer.defaults.DefaultPicoContainer;

/**
 * @author Paul Hammant
 * @version $Revision: 3144 $
 */
public class TestContainer extends DefaultNanoContainer {

    public TestContainer(ComponentFactory caf, PicoContainer parent) {
        super(TestContainer.class.getClassLoader(), caf, parent);
    }

    public TestContainer(PicoContainer parent) {
        super(TestContainer.class.getClassLoader(), new DefaultPicoContainer(new CachingComponentAdapterFactory().forThis(new AnyInjectionComponentAdapterFactory()), parent));
    }

    public TestContainer() {
    }
}
