package org.picocontainer.defaults;

import org.picocontainer.PicoContainer;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.tck.AbstractBasicStringCompatabilityTestCase;
import org.picocontainer.tck.DependsOnTouchable;
import org.picocontainer.tck.SimpleTouchable;


public class DefaultPicoContainerStringTestCase extends AbstractBasicStringCompatabilityTestCase {

    public PicoContainer createPicoContainerWithTouchableAndDependancy() throws DuplicateComponentKeyRegistrationException,
        AssignabilityRegistrationException, NotConcreteRegistrationException, PicoIntrospectionException {

        DefaultPicoContainer defaultPico = new DefaultPicoContainer.Default();
        defaultPico.registerComponent("touchable", SimpleTouchable.class);
        defaultPico.registerComponent("dependsOnTouchable", DependsOnTouchable.class);
        return defaultPico;
    }

    public PicoContainer createPicoContainerWithTouchablesDependancyOnly() throws PicoRegistrationException, PicoIntrospectionException {
        DefaultPicoContainer defaultPico = new DefaultPicoContainer.Default();
        defaultPico.registerComponent("dependsOnTouchable", DependsOnTouchable.class);
        return defaultPico;
    }

    // testXXX methods are in superclass.

}
