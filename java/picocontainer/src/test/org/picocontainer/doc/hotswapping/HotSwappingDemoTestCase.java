package org.picocontainer.doc.hotswapping;

import junit.framework.TestCase;
import org.picocontainer.defaults.CachingComponentAdapterFactory;
import org.picocontainer.defaults.ComponentAdapterFactory;
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
ComponentAdapterFactory caf = new CachingComponentAdapterFactory(new ImplementationHidingComponentAdapterFactory());
DefaultPicoContainer pico = new DefaultPicoContainer(caf);
// Register two classes with mutual dependencies on each other. (See ImplementationHidingComponentAdapterFactoryTestCase)
pico.registerComponentImplementation(Wife.class);
pico.registerComponentImplementation(Husband.class);

Woman woman = (Woman) pico.getComponentInstance(Wife.class);
assertEquals(10, woman.getMan().getEndurance());
// END SNIPPET: circular
// START SNIPPET: hotswap
// let the woman use another man
Man superman = new Superman();
((Swappable)woman.getMan()).hotswap(superman);

assertEquals(1000, woman.getMan().getEndurance());
// END SNIPPET: hotswap
    }

}