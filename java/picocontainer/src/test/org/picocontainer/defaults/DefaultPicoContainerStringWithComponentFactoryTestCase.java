package org.picocontainer.defaults;

import org.picocontainer.PicoContainer;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.ComponentFactory;
import org.picocontainer.tck.AbstractBasicStringCompatabilityTestCase;
import org.picocontainer.tck.DependsOnTouchable;
import org.picocontainer.tck.SimpleTouchable;

public class DefaultPicoContainerStringWithComponentFactoryTestCase extends AbstractBasicStringCompatabilityTestCase {

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

    // testXXX methods are in superclass.

}
