package org.picocontainer.tck;

import junit.framework.TestCase;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoException;
import org.picocontainer.PicoRegistrationException;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public abstract class AbstractLazyInstantiationTestCase extends TestCase {

    protected abstract MutablePicoContainer createPicoContainer();

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

    public void testLazyInstantiation() throws PicoException, PicoRegistrationException {
        MutablePicoContainer pico = createPicoContainer();

        pico.registerComponentImplementation(Kilroy.class);
        pico.registerComponentImplementation(Havana.class);

        assertSame(pico.getComponentInstance(Havana.class), pico.getComponentInstance(Havana.class));
        assertNotNull(pico.getComponentInstance(Havana.class));
        assertEquals("Clean wall", ((Havana) pico.getComponentInstance(Havana.class)).paint);
        assertNotNull(pico.getComponentInstance(Kilroy.class));
        assertEquals("Kilroy was here", ((Havana) pico.getComponentInstance(Havana.class)).paint);
    }
}
