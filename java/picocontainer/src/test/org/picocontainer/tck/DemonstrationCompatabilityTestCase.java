package org.picocontainer.tck;

import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.DuplicateComponentKeyRegistrationException;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.NotConcreteRegistrationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoRegistrationException;
import junit.framework.TestCase;

/**
 * This class should be cloned by TCK users.
 */
public class DemonstrationCompatabilityTestCase extends TestCase {

    // extends AbstractBasicClassCompatabilityTestCase {

    public void testNothing() {
        // remove this method,
        // change the extends above
        // uncomment (and complete) the code below
    }

//    public PicoContainer createPicoContainerWithTouchableAndDependancy() throws DuplicateComponentKeyRegistrationException,
//        AssignabilityRegistrationException, NotConcreteRegistrationException, PicoIntrospectionException {
//
//        PicoContainer pico = new ????
//        pico.registerComponent(Touchable.class, SimpleTouchable.class);
//        pico.registerComponentByClass(DependsOnTouchable.class);
//        return pico;
//    }
//
//    public PicoContainer createPicoContainerWithTouchablesDependancyOnly() throws PicoRegistrationException, PicoIntrospectionException {
//        PicoContainer pico = new ???
//        pico.registerComponentByClass(DependsOnTouchable.class);
//        return pico;
//    }

    // testXXX methods are in superclass.

}
