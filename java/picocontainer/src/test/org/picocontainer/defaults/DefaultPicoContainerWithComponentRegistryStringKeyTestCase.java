package org.picocontainer.defaults;

import org.picocontainer.internals.ComponentRegistry;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.tck.AbstractBasicStringCompatabilityTestCase;
import org.picocontainer.testmodel.DependsOnTouchable;
import org.picocontainer.testmodel.SimpleTouchable;

import java.util.HashMap;

public class DefaultPicoContainerWithComponentRegistryStringKeyTestCase extends AbstractBasicStringCompatabilityTestCase {

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

    protected void addAHashMapByInstance(PicoContainer picoContainer) throws PicoRegistrationException, PicoIntrospectionException {
        ((DefaultPicoContainer) picoContainer).registerComponent("map", new HashMap());
    }


    // testXXX methods are in superclass.

}
