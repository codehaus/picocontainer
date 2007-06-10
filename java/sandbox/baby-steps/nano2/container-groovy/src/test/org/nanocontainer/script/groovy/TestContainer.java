package org.nanocontainer.script.groovy;

import org.nanocontainer.DefaultNanoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.ComponentFactory;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.injectors.AnyInjectionFactory;
import org.picocontainer.behaviors.CachingBehaviorFactory;

/**
 * @author Paul Hammant
 * @version $Revision: 3144 $
 */
public class TestContainer extends DefaultNanoContainer {

    public TestContainer(ComponentFactory caf, PicoContainer parent) {
        super(TestContainer.class.getClassLoader(), caf, parent);
    }

    public TestContainer(PicoContainer parent) {
        super(TestContainer.class.getClassLoader(), new DefaultPicoContainer(new CachingBehaviorFactory().forThis(new AnyInjectionFactory()), parent));
    }

    public TestContainer() {
    }
}
