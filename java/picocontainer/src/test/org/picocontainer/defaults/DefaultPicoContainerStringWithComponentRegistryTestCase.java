package org.picocontainer.defaults;

import org.picocontainer.PicoContainer;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.ComponentFactory;
import org.picocontainer.ComponentRegistry;
import org.picocontainer.tck.AbstractBasicStringCompatabilityTestCase;
import org.picocontainer.tck.DependsOnTouchable;
import org.picocontainer.tck.SimpleTouchable;

public class DefaultPicoContainerStringWithComponentRegistryTestCase extends AbstractBasicStringCompatabilityTestCase {

    public PicoContainer createPicoContainerWithTouchableAndDependancy() throws DuplicateComponentKeyRegistrationException,
        AssignabilityRegistrationException, NotConcreteRegistrationException, PicoIntrospectionException {

        ComponentRegistry componentRegistry = new DefaultComponentRegistry();
        DefaultPicoContainer defaultPico = new DefaultPicoContainer.WithComponentRegistry(componentRegistry);
        defaultPico.registerComponent("touchable", SimpleTouchable.class);
        defaultPico.registerComponent("dependsOnTouchable", DependsOnTouchable.class);
        return defaultPico;
    }

    public PicoContainer createPicoContainerWithTouchablesDependancyOnly() throws PicoRegistrationException, PicoIntrospectionException {
        ComponentRegistry componentRegistry = new DefaultComponentRegistry();
        DefaultPicoContainer defaultPico = new DefaultPicoContainer.WithComponentRegistry(componentRegistry);
        defaultPico.registerComponent("dependsOnTouchable", DependsOnTouchable.class);
        return defaultPico;
    }

    // testXXX methods are in superclass.

}
