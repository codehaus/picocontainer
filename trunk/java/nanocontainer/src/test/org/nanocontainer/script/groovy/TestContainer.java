package org.nanocontainer.script.groovy;

import org.nanocontainer.reflection.DefaultSoftCompositionPicoContainer;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.PicoContainer;

/**
 * @author Paul Hammant
 * @version $Revision$
 */
public class TestContainer extends DefaultSoftCompositionPicoContainer {

    public TestContainer(ComponentAdapterFactory caf, PicoContainer parent) {
        super(TestContainer.class.getClassLoader(), caf, parent);
    }

    public TestContainer(PicoContainer parent) {
        super(TestContainer.class.getClassLoader(), parent);
    }

    public TestContainer() {
    }
}
