package org.picocontainer.doc.caching;

import junit.framework.TestCase;
import org.picocontainer.alternatives.CachingPicoContainer;
import org.picocontainer.alternatives.ImplementationHidingCachingPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.ConstructorInjectionComponentAdapterFactory;
import org.picocontainer.defaults.CachingComponentAdapter;

import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: paul
 * Date: Aug 27, 2005
 * Time: 9:46:23 AM
 * To change this template use File | Settings | File Templates.
 */
public class BasicCachingExampleTestCase extends TestCase {

    public void testCaching() {

        // START SNIPPET: caching
        CachingPicoContainer pico = new CachingPicoContainer();
        pico.registerComponentImplementation(List.class, ArrayList.class);
        // other resitrations

        Object one = pico.getComponentInstanceOfType(List.class);
        Object two = pico.getComponentInstanceOfType(List.class);

        assertSame("instances should be the same", one, two);
        // END SNIPPET: caching

    }

    public void testDefault() {
        // START SNIPPET: noncaching
        DefaultPicoContainer pico = new DefaultPicoContainer();
        pico.registerComponentImplementation(List.class, ArrayList.class);
        // other resitrations

        Object one = pico.getComponentInstanceOfType(List.class);
        Object two = pico.getComponentInstanceOfType(List.class);

        assertSame("instances are be the same by default", one, two);
        // END SNIPPET: noncaching

    }

    public void testNonCaching() {

        // START SNIPPET: noncaching
        DefaultPicoContainer pico = new DefaultPicoContainer(new ConstructorInjectionComponentAdapterFactory());
        pico.registerComponentImplementation(List.class, ArrayList.class);
        // other resitrations

        Object one = pico.getComponentInstanceOfType(List.class);
        Object two = pico.getComponentInstanceOfType(List.class);

        assertNotSame("instances should NOT be the same", one, two);
        // END SNIPPET: noncaching

    }

    public void testImplementationHidingCaching() {

        // START SNIPPET: implhiding
        ImplementationHidingCachingPicoContainer pico = new ImplementationHidingCachingPicoContainer();
        pico.registerComponentImplementation(List.class, ArrayList.class);
        // other resitrations

        Object one = pico.getComponentInstanceOfType(List.class);
        Object two = pico.getComponentInstanceOfType(List.class);

        assertSame("instances should be the same", one, two);

        assertTrue("should not be castable back to implementation",
                        (one instanceof ArrayList) == false);
        // END SNIPPET: implhiding

    }

    public void testFlushingOfCache() {
        // START SNIPPET: caching
        CachingPicoContainer pico = new CachingPicoContainer();
        CachingComponentAdapter cca = (CachingComponentAdapter) pico.registerComponentImplementation(List.class, ArrayList.class);
        // other resitrations

        Object one = pico.getComponentInstanceOfType(List.class);
        cca.flush();
        Object two = pico.getComponentInstanceOfType(List.class);

        assertNotSame("instances should NOT be the same", one, two);
        // END SNIPPET: caching

    }


}
