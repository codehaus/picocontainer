package org.picocontainer.doc.introduction;

import junit.framework.TestCase;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.UnsatisfiableDependenciesException;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class HierarchyTestCase extends TestCase {
    public void testHierarchy() {
        try {
            // START SNIPPET: wontwork
            // Create x hierarchy of containers
            MutablePicoContainer x = new DefaultPicoContainer();
            MutablePicoContainer y = new DefaultPicoContainer( x );
            MutablePicoContainer z = new DefaultPicoContainer( x );

            // Assemble components
            x.registerComponentImplementation(Apple.class);
            y.registerComponentImplementation(Juicer.class);
            z.registerComponentImplementation(Peeler.class);

            // Instantiate components
            Peeler peeler = (Peeler) z.getComponentInstance(Peeler.class);
            // WON'T WORK! peeler will be null
            peeler = (Peeler) x.getComponentInstance(Peeler.class);
            // WON'T WORK! This will throw an exception
            Juicer juicer = (Juicer) y.getComponentInstance(Juicer.class);
            // END SNIPPET: wontwork
        } catch (UnsatisfiableDependenciesException e) {
            e.printStackTrace();
            // expected
        }
    }
}