package org.nanocontainer.reflection;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.AbstractImplementationHidingPicoContainerTestCase;
import org.picocontainer.tck.AbstractPicoContainerTestCase;
import org.nanocontainer.script.groovy.TestComponentAdapterFactory;

/**
 * @author Paul Hammant
 * @version $Revision$
 */

public class ImplementationHidingSoftCompositionPicoContainerTestCase extends AbstractImplementationHidingPicoContainerTestCase {

    protected MutablePicoContainer createImplementationHidingPicoContainer() {
        return new ImplementationHidingSoftCompositionPicoContainer();
    }

    protected MutablePicoContainer createPicoContainer(PicoContainer parent) {
        return new ImplementationHidingSoftCompositionPicoContainer(this.getClass().getClassLoader(), parent);
    }

    public void testAcceptShouldIterateOverChildContainersAndAppropriateComponents() {
        super.testAcceptShouldIterateOverChildContainersAndAppropriateComponents();    //To change body of overridden methods use File | Settings | File Templates.
    }

    // test methods inherited. This container is part compliant.
}
