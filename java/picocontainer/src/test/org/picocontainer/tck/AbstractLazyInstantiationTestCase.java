package org.picocontainer.tck;

import junit.framework.TestCase;
import org.picocontainer.RegistrationPicoContainer;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.PicoInitializationException;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public abstract class AbstractLazyInstantiationTestCase extends TestCase {

    protected abstract RegistrationPicoContainer createRegistrationPicoContainer();

    public static class Kilroy {
        public Kilroy(Havana havana) {
            havana.graffiti("Kilroy was here");
        }
    }

    public static class Havana {
        public String paint = "Clean wall";

        public void graffiti(String paint) {
            this.paint = paint;
        }
    }

    public void testLazyInstantiation() throws PicoInitializationException, PicoRegistrationException {
        RegistrationPicoContainer pico = createRegistrationPicoContainer();

        pico.registerComponentByClass(Kilroy.class);
        pico.registerComponentByClass(Havana.class);

        assertSame(pico.getComponent(Havana.class), pico.getComponent(Havana.class));
        assertNotNull(pico.getComponent(Havana.class));
        assertEquals("Clean wall", ((Havana) pico.getComponent(Havana.class)).paint);
        assertNotNull(pico.getComponent(Kilroy.class));
        assertEquals("Kilroy was here", ((Havana) pico.getComponent(Havana.class)).paint);
    }
}
