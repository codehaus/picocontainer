package org.nanocontainer.webcontainer;

import junit.framework.TestCase;

import java.io.IOException;
import java.net.URL;

import org.picocontainer.defaults.DefaultPicoContainer;
import org.mortbay.util.IO;

import javax.servlet.Filter;

public class DependencyInjectionFilterTestCase extends TestCase {

    PicoJettyServer server;
    protected void tearDown() throws Exception {
        if (server != null) {
            server.stop();
        }
        Thread.sleep(1 * 1000);
    }

    public void testCanInstantiateWebContainerContextAndFilter() throws InterruptedException, IOException {

        final DefaultPicoContainer parentContainer = new DefaultPicoContainer();
        parentContainer.registerComponentInstance(String.class, "Fred");
        parentContainer.registerComponentInstance(Integer.class, new Integer(5));


        server = new PicoJettyServer("localhost", 8080, parentContainer);
        PicoContextHandler barContext = server.createContext("/bar", false);
        PicoFilterHolder filterHolder = barContext.addFilterWithMapping(DependencyInjectionTestFilter.class, "/*", 0);
        filterHolder.setInitParameter("foo", "bau");
        barContext.addServletWithMapping(DependencyInjectionTestServlet.class, "/foo2");
        server.start();

        Thread.sleep(2 * 1000);

        URL url = new URL("http://localhost:8080/bar/foo2");

        assertEquals("hello Fred Filtered!(int= 5 bau)", IO.toString(url.openStream()));


    }

    public void testCanInstantiateWebContainerContextAndFilterInstance() throws InterruptedException, IOException {

        final DefaultPicoContainer parentContainer = new DefaultPicoContainer();
        parentContainer.registerComponentInstance(String.class, "Fred");


        server = new PicoJettyServer("localhost", 8080, parentContainer);
        PicoContextHandler barContext = server.createContext("/bar", false);
        DependencyInjectionTestFilter filter = (DependencyInjectionTestFilter) barContext.addFilterWithMapping(new DependencyInjectionTestFilter(new Integer(5)), "/*", 0);
        filter.setFoo("bau");
        barContext.addServletWithMapping(DependencyInjectionTestServlet.class, "/foo2");
        server.start();

        Thread.sleep(2 * 1000);

        URL url = new URL("http://localhost:8080/bar/foo2");

        assertEquals("hello Fred Filtered!(int= 5 bau)", IO.toString(url.openStream()));


    }


    public void testCanInstantiateWebContainerContextAndServlet() throws InterruptedException, IOException {

        final DefaultPicoContainer parentContainer = new DefaultPicoContainer();
        parentContainer.registerComponentInstance(String.class, "Fred");
        parentContainer.registerComponentInstance(Integer.class, new Integer(5));

        server = new PicoJettyServer("localhost", 8080, parentContainer);
        PicoContextHandler barContext = server.createContext("/bar", false);
        barContext.addFilterWithMapping(DependencyInjectionTestFilter.class, "/*", 0);
        PicoServletHolder holder = barContext.addServletWithMapping(DependencyInjectionTestServlet.class, "/foo2");
        holder.setInitParameter("foo", "bau");
        server.start();

        Thread.sleep(2 * 1000);

        URL url = new URL("http://localhost:8080/bar/foo2");

        assertEquals("hello Fred Filtered!(int= 5) bau", IO.toString(url.openStream()));

    }


}
