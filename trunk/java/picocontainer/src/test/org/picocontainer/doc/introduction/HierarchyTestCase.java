package org.picocontainer.doc.introduction;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.UnsatisfiableDependenciesException;
import junit.framework.TestCase;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class HierarchyTestCase extends TestCase {
    public void testHierarchy() {
        try {
            // Create a hierarchy of containers
            MutablePicoContainer a = new DefaultPicoContainer();
            MutablePicoContainer b = new DefaultPicoContainer(a);
            MutablePicoContainer c = new DefaultPicoContainer(a);

            // Assemble components
            a.registerComponentImplementation(Apple.class);
            b.registerComponentImplementation(Juicer.class);
            c.registerComponentImplementation(Peeler.class);

            // Instantiate components
            Peeler peeler = (Peeler) c.getComponentInstance(Peeler.class);
            // WON'T WORK! peeler will be null
            peeler = (Peeler) a.getComponentInstance(Peeler.class);
            // WON'T WORK! This will throw an exception
            Juicer juicer = (Juicer) b.getComponentInstance(Juicer.class);
        } catch (UnsatisfiableDependenciesException e) {
            e.printStackTrace();
            // expected
        }
    }
}