package org.picocontainer.defaults;

import org.picocontainer.PicoContainer;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.tck.AbstractBasicClassCompatabilityTestCase;
import org.picocontainer.tck.DependsOnTouchable;
import org.picocontainer.tck.SimpleTouchable;
import org.picocontainer.tck.Touchable;

import java.util.HashMap;
import java.util.Map;


public class DefaultPicoContainerClassKeyTestCase extends AbstractBasicClassCompatabilityTestCase {

    public PicoContainer createPicoContainerWithTouchableAndDependancy() throws DuplicateComponentKeyRegistrationException,
        AssignabilityRegistrationException, NotConcreteRegistrationException, PicoIntrospectionException {

        DefaultPicoContainer defaultPico = new DefaultPicoContainer.Default();
        defaultPico.registerComponent(Touchable.class, SimpleTouchable.class);
        defaultPico.registerComponentByClass(DependsOnTouchable.class);
        return defaultPico;
    }

    public PicoContainer createPicoContainerWithTouchablesDependancyOnly() throws PicoRegistrationException, PicoIntrospectionException {
        DefaultPicoContainer defaultPico = new DefaultPicoContainer.Default();
        defaultPico.registerComponentByClass(DependsOnTouchable.class);
        return defaultPico;
    }

    protected void addAnotherSimpleTouchable(PicoContainer picoContainer) throws PicoRegistrationException, PicoIntrospectionException {
        ((DefaultPicoContainer) picoContainer).registerComponent(Touchable.class, SimpleTouchable.class);
    }

    public void testDuplicateRegistration() throws Exception {
        super.testDuplicateRegistration();    //To change body of overriden methods use Options | File Templates.
    }

    protected void addAHashMapByInstance(PicoContainer picoContainer) throws PicoRegistrationException, PicoIntrospectionException {
        ((DefaultPicoContainer) picoContainer).registerComponent(Map.class, new HashMap());
    }

    public void testByInstanceRegistration() throws PicoRegistrationException, PicoInitializationException {
        super.testByInstanceRegistration();
    }
    // testXXX methods are in superclass.

}
