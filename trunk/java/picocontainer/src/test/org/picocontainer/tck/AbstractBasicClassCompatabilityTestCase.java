package org.picocontainer.tck;

import junit.framework.TestCase;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.RegistrationPicoContainer;
import org.picocontainer.testmodel.DependsOnTouchable;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.testmodel.Touchable;
import org.picocontainer.defaults.DuplicateComponentKeyRegistrationException;
import org.picocontainer.defaults.NoSatisfiableConstructorsException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.HashMap;

public abstract class AbstractBasicClassCompatabilityTestCase extends TestCase {

    protected abstract RegistrationPicoContainer createClassRegistrationPicoContainer();

    protected final RegistrationPicoContainer createPicoContainerWithTouchableAndDependency() throws
            PicoRegistrationException, PicoIntrospectionException {
        RegistrationPicoContainer pico = createClassRegistrationPicoContainer();

        pico.registerComponent(Touchable.class, SimpleTouchable.class);
        pico.registerComponentByClass(DependsOnTouchable.class);
        return pico;
    }

    protected final RegistrationPicoContainer createPicoContainerWithDependsOnTouchableOnly() throws
            PicoRegistrationException, PicoIntrospectionException {
        RegistrationPicoContainer pico = createClassRegistrationPicoContainer();
        pico.registerComponentByClass(DependsOnTouchable.class);
        return pico;

    }

    public void testNotNull() throws PicoRegistrationException, PicoIntrospectionException {
        assertNotNull("Are you calling super.setUp() in your setUp method?", createPicoContainerWithTouchableAndDependency());
    }

    public void testBasicInstantiationAndContainment() throws PicoInitializationException, PicoRegistrationException {
        PicoContainer picoContainer = createPicoContainerWithTouchableAndDependency();
//        picoContainer.instantiateComponents();
        assertTrue("Container should have Touchable component",
                picoContainer.hasComponent(Touchable.class));
        assertTrue("Container should have DependsOnTouchable component",
                picoContainer.hasComponent(DependsOnTouchable.class));
        assertTrue("Component should be instance of Touchable",
                picoContainer.getComponent(Touchable.class) instanceof Touchable);
        assertTrue("Component should be instance of DependsOnTouchable",
                picoContainer.getComponent(DependsOnTouchable.class) instanceof DependsOnTouchable);
        assertTrue("should not have non existent component", !picoContainer.hasComponent(Map.class));
    }

    public void testSerializabilityOfContainer() throws PicoRegistrationException, PicoInitializationException,
            IOException, ClassNotFoundException {

        PicoContainer picoContainer = createPicoContainerWithTouchableAndDependency();
//        picoContainer.instantiateComponents();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);

        oos.writeObject(picoContainer);

        // yeah yeah, is not needed.
        picoContainer = null;

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));

        picoContainer = (PicoContainer) ois.readObject();

        DependsOnTouchable dependsOnTouchable = (DependsOnTouchable) picoContainer.getComponent(DependsOnTouchable.class);
        assertNotNull(dependsOnTouchable);
        SimpleTouchable touchable = (SimpleTouchable) picoContainer.getComponent(Touchable.class);

        assertTrue("hello should have been called in Touchable", touchable.wasTouched);
    }

    public void testTooFewComponents() throws PicoInitializationException, PicoRegistrationException {

        PicoContainer picoContainer = createPicoContainerWithDependsOnTouchableOnly();

        try {
            picoContainer.getComponent(DependsOnTouchable.class);
            fail("should need a Touchable");
        } catch (NoSatisfiableConstructorsException e) {
            // expected
        }
    }

    public void testDoubleInstantiation() throws PicoRegistrationException, PicoInitializationException {
        PicoContainer picoContainer = createPicoContainerWithTouchableAndDependency();
        assertSame(
                picoContainer.getComponent(DependsOnTouchable.class),
                picoContainer.getComponent(DependsOnTouchable.class)
        );
    }

    protected final void addAnotherSimpleTouchable(RegistrationPicoContainer picoContainer) throws PicoRegistrationException, PicoIntrospectionException {
        picoContainer.registerComponent(Touchable.class, SimpleTouchable.class);
    }

    public void testDuplicateRegistration() throws Exception {
        RegistrationPicoContainer picoContainer = createPicoContainerWithTouchableAndDependency();
        try {
            addAnotherSimpleTouchable(picoContainer);
            //picoContainer.instantiateComponents();
            fail("Should have barfed with dupe registration");
        } catch (DuplicateComponentKeyRegistrationException e) {
            // expected
            assertTrue("Wrong key", e.getDuplicateKey() == Touchable.class);
        }
    }

    protected final void addAHashMapByInstance(RegistrationPicoContainer picoContainer) throws PicoRegistrationException, PicoIntrospectionException {
        picoContainer.registerComponent(Map.class, new HashMap());
    }

    public void testByInstanceRegistration() throws PicoRegistrationException, PicoInitializationException {
        RegistrationPicoContainer picoContainer = createPicoContainerWithTouchableAndDependency();
        addAHashMapByInstance(picoContainer);
//        picoContainer.instantiateComponents();
        assertEquals("Wrong number of comps in the internals", 3, picoContainer.getComponents().size());
        assertEquals("Key - Map, Impl - HashMap should be in internals", HashMap.class, picoContainer.getComponent(Map.class).getClass());
        //TODO - some way to test hashmap was passed in as an instance ?
        // should unmanaged side of DefaultPicoContainer be more exposed thru interface?
    }

}
