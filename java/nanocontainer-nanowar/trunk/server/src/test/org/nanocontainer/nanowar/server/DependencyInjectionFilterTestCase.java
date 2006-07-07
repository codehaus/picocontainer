package org.nanocontainer.nanowar.server;

import junit.framework.TestCase;

import java.io.IOException;
import java.net.URL;

import org.picocontainer.defaults.DefaultPicoContainer;
import org.mortbay.io.IO;

public class DependencyInjectionFilterTestCase extends TestCase {

    public void testCanInstantiateWebContainerContextAndServlet() throws InterruptedException, IOException {

        final DefaultPicoContainer parentContainer = new DefaultPicoContainer();
        parentContainer.registerComponentInstance(String.class, "Fred");
        parentContainer.registerComponentInstance(Integer.class, new Integer(5));

        JettyServerPicoEdition server = new JettyServerPicoEdition("localhost", 8080, parentContainer);
        ContextHandlerPicoEdition barContext = server.createContext("/bar");
        barContext.addFilterWithMapping(DependencyInjectionTestFilter.class, "/*", 0);
        barContext.addServletWithMapping(DependencyInjectionTestServlet.class, "/foo2");

        server.start();

        Thread.sleep(2 * 1000);

        URL url = new URL("http://localhost:8080/bar/foo2");

        assertEquals("hello Fred Filtered!(int= 5)", IO.toString(url.openStream()));


    }


}
