package org.picocontainer.defaults;

import org.picocontainer.PicoContainer;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.tck.AbstractBasicStringCompatabilityTestCase;
import org.picocontainer.tck.DependsOnTouchable;
import org.picocontainer.tck.SimpleTouchable;

import java.util.HashMap;


public class DefaultPicoContainerStringKeyTestCase extends AbstractBasicStringCompatabilityTestCase {

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

    protected void addAHashMapByInstance(PicoContainer picoContainer) throws PicoRegistrationException, PicoIntrospectionException {
        ((DefaultPicoContainer) picoContainer).registerComponent("map", new HashMap());
    }


    // testXXX methods are in superclass.

}
