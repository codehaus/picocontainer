package org.picocontainer.tck;

import junit.framework.TestCase;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.defaults.UnsatisfiedDependencyInstantiationException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public abstract class AbstractBasicStringCompatabilityTestCase extends TestCase {

    abstract public PicoContainer createPicoContainerWithTouchableAndDependancy() throws
        PicoRegistrationException, PicoIntrospectionException;
    abstract public PicoContainer createPicoContainerWithTouchablesDependancyOnly() throws
        PicoRegistrationException, PicoIntrospectionException;

    public void testNotNull() throws PicoRegistrationException, PicoIntrospectionException {
        assertNotNull("Are you calling super.setUp() in your setUp method?", createPicoContainerWithTouchableAndDependancy());
    }

    public void testBasicInstantiationAndContainment() throws PicoInitializationException, PicoRegistrationException {
        PicoContainer picoContainer = createPicoContainerWithTouchableAndDependancy();
        picoContainer.instantiateComponents();
        assertTrue("Container should have Touchable component",
                picoContainer.hasComponent("touchable"));
        assertTrue("Container should have DependsOnTouchable component",
                picoContainer.hasComponent("dependsOnTouchable"));
        assertTrue("Component should be instance of Touchable",
                picoContainer.getComponent("touchable") instanceof Touchable);
        assertTrue("Component should be instance of DependsOnTouchable",
                picoContainer.getComponent("dependsOnTouchable") instanceof DependsOnTouchable);
        assertTrue("should not have non existent component", !picoContainer.hasComponent("doesNotExist"));
    }

}
