package org.nanocontainer.reflection;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.tck.AbstractPicoContainerTestCase;
import org.nanocontainer.script.groovy.TestComponentAdapterFactory;

/**
 * @author Paul Hammant
 * @version $Revision$
 */
public class DefaultSoftCompositionPicoContainerTestCase extends AbstractPicoContainerTestCase {

    protected MutablePicoContainer createPicoContainer(PicoContainer parent) {
        return new DefaultSoftCompositionPicoContainer(this.getClass().getClassLoader(), parent);
    }

    // test methods inherited. This container is fully compliant.
}
