package org.picocontainer.tck;

import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.DuplicateComponentKeyRegistrationException;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.NotConcreteRegistrationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoContainer;


public class DemonstrationCompatabilityTestCase extends AbstractBasicCompatabilityTestCase {

    public PicoContainer createPicoContainer() throws DuplicateComponentKeyRegistrationException,
        AssignabilityRegistrationException, NotConcreteRegistrationException, PicoIntrospectionException {
        DefaultPicoContainer defaultPico = new DefaultPicoContainer.Default();
        defaultPico.registerComponent(Touchable.class, SimpleTouchable.class);
        defaultPico.registerComponentByClass(DependsOnTouchable.class);
        return defaultPico;
    }

    // testXXX methods are in superclass.

}
