/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/

package org.nanocontainer.nanowar.server;

import junit.framework.TestCase;
import org.mortbay.io.IO;
import org.picocontainer.defaults.DefaultPicoContainer;

import java.net.URL;
import java.net.MalformedURLException;
import java.io.IOException;

public class DependencyInjectionServletTestCase extends TestCase {

    public void testCanInstantiateWebContainerContextAndServlet() throws InterruptedException, IOException {

        final DefaultPicoContainer parentContainer = new DefaultPicoContainer();
        parentContainer.registerComponentInstance(String.class, "Fred");

        JettyServerPicoEdition server = new JettyServerPicoEdition("localhost", 8080);
        ContextHandlerPicoEdition barContext = server.createContext("/bar");
        barContext.addServletWithMapping(DependencyInjectionTestServlet.class, "/foo", parentContainer);

        server.start();

        Thread.sleep(2 * 1000);

        URL url = new URL("http://localhost:8080/bar/foo");
        assertEquals("hello Fred", IO.toString(url.openStream()));

        //Thread.sleep(50 * 1000);


    }


}