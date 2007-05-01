package org.picocontainer.doc.caching;

import junit.framework.TestCase;
import org.picocontainer.alternatives.CachingPicoContainer;
import org.picocontainer.alternatives.ImplementationHidingCachingPicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.componentadapters.ConstructorInjectionComponentAdapterFactory;
import org.picocontainer.componentadapters.CachingComponentAdapter;
import org.picocontainer.componentadapters.CachingComponentAdapterFactory;

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

    public void testCachingContainerCaches() {

        // START SNIPPET: caching
        CachingPicoContainer pico = new CachingPicoContainer();
        pico.component(List.class, ArrayList.class);
        // other resitrations

        Object one = pico.getComponent(List.class);
        Object two = pico.getComponent(List.class);

        assertSame("instances should be the same", one, two);
        // END SNIPPET: caching

    }

    public void testCachingContainerWithCAFStillCaches() {

        // START SNIPPET: caching2
        CachingPicoContainer pico = new CachingPicoContainer(new ConstructorInjectionComponentAdapterFactory());
        pico.component(List.class, ArrayList.class);
        // other resitrations

        Object one = pico.getComponent(List.class);
        Object two = pico.getComponent(List.class);

        assertSame("instances should be the same", one, two);
        // END SNIPPET: caching2

    }

    public void testDefaulCaching() {
        // START SNIPPET: default
        DefaultPicoContainer pico = new DefaultPicoContainer();
        pico.component(List.class, ArrayList.class);
        // other resitrations

        Object one = pico.getComponent(List.class);
        Object two = pico.getComponent(List.class);

        assertSame("instances should be the same by default", one, two);
        // END SNIPPET: default

    }

    public void testDefaulCachingtheHardWay() {
        // START SNIPPET: default2
        DefaultPicoContainer pico = new DefaultPicoContainer(
                        new CachingComponentAdapterFactory(new ConstructorInjectionComponentAdapterFactory()));
        pico.component(List.class, ArrayList.class);
        // other resitrations

        Object one = pico.getComponent(List.class);
        Object two = pico.getComponent(List.class);

        assertSame("instances should be the same", one, two);
        // END SNIPPET: default2

    }

    public void testDefaultWithCAFNonCaching() {

        // START SNIPPET: default-noncaching
        DefaultPicoContainer pico = new DefaultPicoContainer(new ConstructorInjectionComponentAdapterFactory());
        pico.component(List.class, ArrayList.class);
        // other resitrations

        Object one = pico.getComponent(List.class);
        Object two = pico.getComponent(List.class);

        assertNotSame("instances should NOT be the same", one, two);
        // END SNIPPET: default-noncaching

    }

    public void testImplementationHidingCaching() {

        // START SNIPPET: implhiding
        ImplementationHidingCachingPicoContainer pico = new ImplementationHidingCachingPicoContainer();
        pico.component(List.class, ArrayList.class);
        // other resitrations

        Object one = pico.getComponent(List.class);
        Object two = pico.getComponent(List.class);

        assertSame("instances should be the same", one, two);

        assertTrue("should not be castable back to implementation",
                        (one instanceof ArrayList) == false);
        // END SNIPPET: implhiding

    }

    public void testFlushingOfCache() {
        // START SNIPPET: caching
        CachingPicoContainer pico = new CachingPicoContainer();
        CachingComponentAdapter cca = (CachingComponentAdapter) pico.component(List.class, ArrayList.class).lastCA();
        // other resitrations

        Object one = pico.getComponent(List.class);
        cca.flush();
        Object two = pico.getComponent(List.class);

        assertNotSame("instances should NOT be the same", one, two);
        // END SNIPPET: caching

    }


}
