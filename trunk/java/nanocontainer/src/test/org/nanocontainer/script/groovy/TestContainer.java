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
        super(caf, parent);
    }

    public TestContainer(PicoContainer parent) {
        super(parent);
    }

    public TestContainer() {
    }
}
