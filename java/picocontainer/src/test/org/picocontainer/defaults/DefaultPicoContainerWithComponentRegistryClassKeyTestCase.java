package org.picocontainer.defaults;

import org.picocontainer.ComponentRegistry;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.tck.AbstractBasicClassCompatabilityTestCase;
import org.picocontainer.tck.DependsOnTouchable;
import org.picocontainer.tck.SimpleTouchable;
import org.picocontainer.tck.Touchable;

import java.util.Map;
import java.util.HashMap;


public class DefaultPicoContainerWithComponentRegistryClassKeyTestCase extends AbstractBasicClassCompatabilityTestCase {

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

    protected void addAHashMapByInstance(PicoContainer picoContainer) throws PicoRegistrationException, PicoIntrospectionException {
        ((DefaultPicoContainer) picoContainer).registerComponent(Map.class, new HashMap());
    }    

    // testXXX methods are in superclass.

}
