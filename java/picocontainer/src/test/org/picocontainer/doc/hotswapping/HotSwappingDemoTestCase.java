package org.picocontainer.doc.hotswapping;

import junit.framework.TestCase;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.defaults.DefaultComponentAdapterFactory;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.ImplementationHidingComponentAdapterFactory;
import org.picocontainer.defaults.Swappable;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class HotSwappingDemoTestCase extends TestCase {
    public void testDemo() {
// START SNIPPET: circular
ComponentAdapterFactory caf = new ImplementationHidingComponentAdapterFactory(new DefaultComponentAdapterFactory());
DefaultPicoContainer pico = new DefaultPicoContainer(caf);
// Register two classes with mutual dependencies on each other. (See ImplementationHidingComponentAdapterFactoryTestCase)
pico.registerComponentImplementation(Wife.class);
pico.registerComponentImplementation(Husband.class);

// START SNIPPET: hotswap
Woman woman = (Woman) pico.getComponentInstance(Wife.class);
// END SNIPPET: circular

assertEquals(10, woman.getMan().getEndurance());

// let the woman use another man
Man superman = new Superman();
((Swappable)woman.getMan()).__hotSwap(superman);

assertEquals(1000, woman.getMan().getEndurance());
// END SNIPPET: hotswap
    }

}