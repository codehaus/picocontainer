package org.picocontainer.tck;

import junit.framework.TestCase;
import org.picocontainer.*;
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

/**
 * This test tests (at least it should) test all the method in MutablePicoContainer
 */
public abstract class AbstractPicoContainerTestCase extends TestCase {

    protected abstract MutablePicoContainer createPicoContainer();

    protected final MutablePicoContainer createPicoContainerWithTouchableAndDependency() throws
            PicoRegistrationException, PicoIntrospectionException {
        MutablePicoContainer pico = createPicoContainer();

        pico.registerComponentImplementation(Touchable.class, SimpleTouchable.class);
        pico.registerComponentImplementation(DependsOnTouchable.class);
        return pico;
    }

    protected final PicoContainer createPicoContainerWithDependsOnTouchableOnly() throws
            PicoRegistrationException, PicoIntrospectionException {
        MutablePicoContainer pico = createPicoContainer();
        pico.registerComponentImplementation(DependsOnTouchable.class);
        return pico;

    }

    public void testNotNull() throws PicoRegistrationException, PicoIntrospectionException {
        assertNotNull("Are you calling super.setUp() in your setUp method?", createPicoContainerWithTouchableAndDependency());
    }

    public void testBasicInstantiationAndContainment() throws PicoException, PicoRegistrationException {
        PicoContainer pico = createPicoContainerWithTouchableAndDependency();

        assertTrue("Container should have Touchable component",
                pico.hasComponent(Touchable.class));
        assertTrue("Container should have DependsOnTouchable component",
                pico.hasComponent(DependsOnTouchable.class));
        assertTrue("Component should be instance of Touchable",
                pico.getComponentInstance(Touchable.class) instanceof Touchable);
        assertTrue("Component should be instance of DependsOnTouchable",
                pico.getComponentInstance(DependsOnTouchable.class) instanceof DependsOnTouchable);
        assertTrue("should not have non existent component", !pico.hasComponent(Map.class));
    }

    public void testInstanceRegistration() throws PicoException, PicoInitializationException {
        MutablePicoContainer pico = createPicoContainer();
        StringBuffer sb = new StringBuffer();
        pico.registerComponentInstance(sb);
        assertSame(sb, pico.getComponentInstance(StringBuffer.class));
    }

    public void testSerializabilityOfContainer() throws PicoException, PicoInitializationException,
            IOException, ClassNotFoundException {

        PicoContainer pico = createPicoContainerWithTouchableAndDependency();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);

        oos.writeObject(pico);

        // yeah yeah, is not needed.
        pico = null;

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));

        pico = (PicoContainer) ois.readObject();

        DependsOnTouchable dependsOnTouchable = (DependsOnTouchable) pico.getComponentInstance(DependsOnTouchable.class);
        assertNotNull(dependsOnTouchable);
        SimpleTouchable touchable = (SimpleTouchable) pico.getComponentInstance(Touchable.class);

        assertTrue("hello should have been called in Touchable", touchable.wasTouched);
    }

    public void testTooFewComponents() throws PicoException, PicoRegistrationException {

        PicoContainer picoContainer = createPicoContainerWithDependsOnTouchableOnly();

        try {
            picoContainer.getComponentInstance(DependsOnTouchable.class);
            fail("should need a Touchable");
        } catch (NoSatisfiableConstructorsException e) {
            assertEquals(DependsOnTouchable.class, e.getUnsatisfiableComponentImplementation());
        }
    }

    public void testDoubleInstantiation() throws PicoException {
        PicoContainer pico = createPicoContainerWithTouchableAndDependency();
        assertSame(
                pico.getComponentInstance(DependsOnTouchable.class),
                pico.getComponentInstance(DependsOnTouchable.class)
        );
    }

    public void testDuplicateRegistration() throws Exception {
        MutablePicoContainer pico = createPicoContainerWithTouchableAndDependency();
        try {
            pico.registerComponentImplementation(Touchable.class, SimpleTouchable.class);
            fail("Should have barfed with dupe registration");
        } catch (DuplicateComponentKeyRegistrationException e) {
            assertTrue("Wrong key", e.getDuplicateKey() == Touchable.class);
        }
    }

    public void testByInstanceRegistration() throws PicoException, PicoInitializationException {
        MutablePicoContainer pico = createPicoContainerWithTouchableAndDependency();
        pico.registerComponentInstance(Map.class, new HashMap());
        assertEquals("Wrong number of comps in the internals", 3, pico.getComponentInstances().size());
        assertEquals("Key - Map, Impl - HashMap should be in internals", HashMap.class, pico.getComponentInstance(Map.class).getClass());
    }

}
