package org.picocontainer.defaults;

import org.picocontainer.PicoContainer;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.internals.ComponentFactory;
import org.picocontainer.tck.AbstractBasicStringCompatabilityTestCase;
import org.picocontainer.testmodel.DependsOnTouchable;
import org.picocontainer.testmodel.SimpleTouchable;

import java.util.Map;
import java.util.HashMap;

public class DefaultPicoContainerWithComponentFactoryStringKeyTestCase extends AbstractBasicStringCompatabilityTestCase {

    public PicoContainer createPicoContainerWithTouchableAndDependancy() throws DuplicateComponentKeyRegistrationException,
            AssignabilityRegistrationException, NotConcreteRegistrationException, PicoIntrospectionException {

        ComponentFactory componentFactory = new DefaultComponentFactory();
        DefaultPicoContainer defaultPico = new DefaultPicoContainer.WithComponentFactory(componentFactory);
        defaultPico.registerComponent("touchable", SimpleTouchable.class);
        defaultPico.registerComponent("dependsOnTouchable", DependsOnTouchable.class);
        return defaultPico;
    }

    public PicoContainer createPicoContainerWithTouchablesDependancyOnly() throws PicoRegistrationException, PicoIntrospectionException {
        ComponentFactory componentFactory = new DefaultComponentFactory();
        DefaultPicoContainer defaultPico = new DefaultPicoContainer.WithComponentFactory(componentFactory);
        defaultPico.registerComponent("dependsOnTouchable", DependsOnTouchable.class);
        return defaultPico;
    }

    protected void addAHashMapByInstance(PicoContainer picoContainer) throws PicoRegistrationException, PicoIntrospectionException {
        ((DefaultPicoContainer) picoContainer).registerComponent("map", new HashMap());
    }

    // testXXX methods are in superclass.

}
