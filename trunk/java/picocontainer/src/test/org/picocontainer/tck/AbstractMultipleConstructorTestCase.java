package org.picocontainer.tck;

import junit.framework.TestCase;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoException;
import org.picocontainer.defaults.TooManySatisfiableConstructorsException;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public abstract class AbstractMultipleConstructorTestCase extends TestCase {

    protected abstract MutablePicoContainer createPicoContainer();

    public static class Multi {
        public String message;

        public Multi(One one, Two two, Three three) {
            message = "one two three";
        }

        public Multi(One one, Two two) {
            message = "one two";
        }

        public Multi(Two two, One one) {
            message = "two one";
        }

        public Multi(Two two, Three three) {
            message = "two three";
        }

        public Multi(Three three, One one) {
            message = "three one";
        }
    }

    public static class One {}
    public static class Two {}
    public static class Three {}

    public void testStringWorks() throws PicoException, PicoRegistrationException {
        MutablePicoContainer pico = createPicoContainer();
        pico.registerComponentImplementation(String.class);
        assertEquals("", pico.getComponentInstance(String.class));
    }

    public void testMultiWithOnlySmallSatisfiedDependencyWorks() throws PicoException, PicoRegistrationException {
        MutablePicoContainer pico = createPicoContainer();
        pico.registerComponentImplementation(Multi.class);
        pico.registerComponentImplementation(One.class);
        pico.registerComponentImplementation(Three.class);

        Multi multi = (Multi) pico.getComponentInstance(Multi.class);
        assertEquals("three one", multi.message);
    }

    public void testMultiWithBothSatisfiedDependencyWorks() throws PicoException, PicoRegistrationException {
        MutablePicoContainer pico = createPicoContainer();
        pico.registerComponentImplementation(Multi.class);
        pico.registerComponentImplementation(One.class);
        pico.registerComponentImplementation(Two.class);
        pico.registerComponentImplementation(Three.class);

        Multi multi = (Multi) pico.getComponentInstance(Multi.class);
        assertEquals("one two three", multi.message);
    }

    public void testMultiWithTwoEquallyBigSatisfiedDependenciesFails() throws PicoException, PicoRegistrationException {
        MutablePicoContainer pico = createPicoContainer();
        pico.registerComponentImplementation(Multi.class);
        pico.registerComponentImplementation(One.class);
        pico.registerComponentImplementation(Two.class);

        try {
            Multi multi = (Multi) pico.getComponentInstance(Multi.class);
            fail();
        } catch (TooManySatisfiableConstructorsException e) {
            assertTrue(e.getMessage().indexOf("Three") == -1);
            assertEquals(2, e.getConstructors().size());
            assertEquals(Multi.class, e.getForImplementationClass());
        }
    }
}
