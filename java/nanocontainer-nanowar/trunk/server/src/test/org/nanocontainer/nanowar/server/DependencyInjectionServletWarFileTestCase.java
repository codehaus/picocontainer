package org.nanocontainer.nanowar.server;

import junit.framework.TestCase;

import java.io.IOException;
import java.io.File;
import java.net.URL;

import org.picocontainer.defaults.DefaultPicoContainer;
import org.mortbay.io.IO;

public class DependencyInjectionServletWarFileTestCase extends TestCase {

    public void testCanInstantiateWebContainerContextAndServlet()
            throws InterruptedException, IOException {

        final DefaultPicoContainer parentContainer = new DefaultPicoContainer();
        parentContainer.registerComponentInstance(String.class, "Fred");

        JettyServerPicoEdition server = new JettyServerPicoEdition("localhost", 8080, parentContainer);
        WebAppContextPicoEdition wah = server.addWebApplication("/bar", "/Users/paul/scm/oss/pico2/java/nanocontainer-nanowar/trunk/testwar.war");

        server.start();

        Thread.sleep(2 * 1000);

        URL url = new URL("http://localhost:8080/bar/foo");
        assertEquals("hello Fred", IO.toString(url.openStream()));

        //Thread.sleep(50 * 1000);

        server.stop();

    }


}
