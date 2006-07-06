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

        JettyServerPicoEdition server = new JettyServerPicoEdition("localhost", 8080, parentContainer);
        ContextHandlerPicoEdition barContext = server.createContext("/bar");
        //barContext.addFilterWithMapping(DependencyInjectionTestFilter.class, "/*", 0, parentContainer);
        barContext.addServletWithMapping(DependencyInjectionTestServlet.class, "/foo");

        server.start();

        Thread.sleep(2 * 1000);

        URL url = new URL("http://localhost:8080/bar/foo");
        assertEquals("hello Fred", IO.toString(url.openStream()));

        //Thread.sleep(50 * 1000);


    }


}
