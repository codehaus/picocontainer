package org.picocontainer.defaults;

import org.picocontainer.ComponentRegistry;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.tck.AbstractBasicClassCompatabilityTestCase;
import org.picocontainer.tck.DependsOnTouchable;
import org.picocontainer.tck.SimpleTouchable;
import org.picocontainer.tck.Touchable;


public class DefaultPicoContainerWithComponentRegistryTestCase extends AbstractBasicClassCompatabilityTestCase {

    private ComponentRegistry componentRegistry = new DefaultComponentRegistry();

    public PicoContainer createPicoContainerWithTouchableAndDependancy() throws DuplicateComponentKeyRegistrationException,
        AssignabilityRegistrationException, NotConcreteRegistrationException, PicoIntrospectionException {

        DefaultPicoContainer defaultPico = new DefaultPicoContainer.WithComponentRegistry(componentRegistry);
        defaultPico.registerComponent(Touchable.class, SimpleTouchable.class);
        defaultPico.registerComponentByClass(DependsOnTouchable.class);
        return defaultPico;
    }

    public PicoContainer createPicoContainerWithTouchablesDependancyOnly() throws PicoRegistrationException, PicoIntrospectionException {
        DefaultPicoContainer defaultPico = new DefaultPicoContainer.WithComponentRegistry(componentRegistry);
        defaultPico.registerComponentByClass(DependsOnTouchable.class);
        return defaultPico;
    }

    protected void addAnotherSimpleTouchable(PicoContainer picoContainer) throws PicoRegistrationException, PicoIntrospectionException {
        ((DefaultPicoContainer) picoContainer).registerComponent(Touchable.class, SimpleTouchable.class);
    }

    // testXXX methods are in superclass.

}
