package org.nanocontainer.reflection;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.tck.AbstractPicoContainerTestCase;

/**
 * @author Paul Hammant
 * @version $Revision$
 */
public class SoftCompositionPicoContainerTestCase extends AbstractPicoContainerTestCase {

    protected MutablePicoContainer createPicoContainer(PicoContainer parent) {
        return new SoftCompositionPicoContainer(parent);
    }

    // test methods inherited. This container is fully compliant.
}
