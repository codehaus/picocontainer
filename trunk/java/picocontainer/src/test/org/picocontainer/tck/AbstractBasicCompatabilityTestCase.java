package org.picocontainer.tck;

import junit.framework.TestCase;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.defaults.NotConcreteRegistrationException;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.DuplicateComponentKeyRegistrationException;
import org.picocontainer.defaults.PicoInvocationTargetInitializationException;
import org.picocontainer.defaults.DefaultPicoContainer;

import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.ByteArrayInputStream;

public abstract class AbstractBasicCompatabilityTestCase extends TestCase {

    protected PicoContainer picoContainer;

    protected void setUp() throws Exception {
        picoContainer = createPicoContainer();
    }

    abstract public PicoContainer createPicoContainer() throws Exception;

    public void testNotNull() {
        assertNotNull("Are you calling super.setUp() in your setUp method?", picoContainer);
    }

    public void testBasicInstantiationAndContainment() throws PicoInitializationException {
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

    public void testSerializabilityOfContainer() throws NotConcreteRegistrationException,
        AssignabilityRegistrationException, PicoInitializationException,
        DuplicateComponentKeyRegistrationException, PicoInvocationTargetInitializationException,
        IOException, ClassNotFoundException {

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


}
