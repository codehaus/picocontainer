package org.picocontainer.tck;

import junit.framework.TestCase;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.RegistrationPicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.CannotDecideWhatConstructorToUseException;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public abstract class AbstractMultipleConstructorTestCase extends TestCase {

    protected abstract RegistrationPicoContainer createRegistrationPicoContainer();

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

    public void testStringWorks() throws PicoInitializationException, PicoRegistrationException {
        RegistrationPicoContainer pico = createRegistrationPicoContainer();
        pico.registerComponentByClass(String.class);
        assertEquals("", pico.getComponent(String.class));
    }

    public void testMultiWithOnlySmallSatisfiedDependencyWorks() throws PicoInitializationException, PicoRegistrationException {
        RegistrationPicoContainer pc = createRegistrationPicoContainer();
        pc.registerComponentByClass(Multi.class);
        pc.registerComponentByClass(One.class);
        pc.registerComponentByClass(Three.class);

        Multi multi = (Multi) pc.getComponent(Multi.class);
        assertEquals("three one", multi.message);
    }

    public void testMultiWithBothSatisfiedDependencyWorks() throws PicoInitializationException, PicoRegistrationException {
        RegistrationPicoContainer pc = createRegistrationPicoContainer();
        pc.registerComponentByClass(Multi.class);
        pc.registerComponentByClass(One.class);
        pc.registerComponentByClass(Two.class);
        pc.registerComponentByClass(Three.class);

        Multi multi = (Multi) pc.getComponent(Multi.class);
        assertEquals("one two three", multi.message);
    }

    public void testMultiWithTwoEquallyBigSatisfiedDependenciesFails() throws PicoInitializationException, PicoRegistrationException {
        RegistrationPicoContainer pc = createRegistrationPicoContainer();
        pc.registerComponentByClass(Multi.class);
        pc.registerComponentByClass(One.class);
        pc.registerComponentByClass(Two.class);

        try {
            Multi multi = (Multi) pc.getComponent(Multi.class);
            fail();
        } catch (CannotDecideWhatConstructorToUseException e) {
        }
    }
}
