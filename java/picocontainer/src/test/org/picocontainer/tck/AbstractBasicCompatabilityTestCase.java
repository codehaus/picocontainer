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

public abstract class AbstractBasicCompatabilityTestCase extends TestCase {

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
                picoContainer.hasComponent(Touchable.class));
        assertTrue("Container should have DependsOnTouchable component",
                picoContainer.hasComponent(DependsOnTouchable.class));
        assertTrue("Component should be instance of Touchable",
                picoContainer.getComponent(Touchable.class) instanceof Touchable);
        assertTrue("Component should be instance of DependsOnTouchable",
                picoContainer.getComponent(DependsOnTouchable.class) instanceof DependsOnTouchable);
    }

    public void testSerializabilityOfContainer() throws PicoRegistrationException, PicoInitializationException,
        IOException, ClassNotFoundException {

        PicoContainer picoContainer = createPicoContainerWithTouchableAndDependancy();
        picoContainer.instantiateComponents();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);

        oos.writeObject(picoContainer);

        // yeah yeah, is not needed.
        picoContainer = null;

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));

        picoContainer = (PicoContainer) ois.readObject();

        SimpleTouchable touchable = (SimpleTouchable) picoContainer.getComponent(Touchable.class);

        assertTrue("hello should have been called in Touchable", touchable.wasTouched);
    }

    public void testTooFewComponents() throws PicoInitializationException, PicoRegistrationException {

        PicoContainer picoContainer = createPicoContainerWithTouchablesDependancyOnly();

        try {
            picoContainer.instantiateComponents();
            fail("should need a Touchable");
        } catch (UnsatisfiedDependencyInstantiationException e) {
            // expected
            assertTrue(e.getClassThatNeedsDeps() == DependsOnTouchable.class);
            assertTrue(e.getMessage().indexOf(DependsOnTouchable.class.getName()) > 0);

        }
    }


}
