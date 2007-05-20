/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *                                                                           *
 *****************************************************************************/

package org.nanocontainer.webcontainer.groovy;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.nanocontainer.script.groovy.GroovyContainerBuilder;
import org.nanocontainer.webcontainer.TestHelper;
import org.nanocontainer.webcontainer.WebContainerTestCase;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.ObjectReference;
import org.picocontainer.defaults.SimpleReference;

public class WebContainerBuilderTestCase extends WebContainerTestCase {

    private ObjectReference containerRef = new SimpleReference();
    private ObjectReference parentContainerRef = new SimpleReference();

    private PicoContainer pico;

    protected void tearDown() throws Exception {
        if (pico != null) {
            pico.stop();
        }
    }

    public void testCanComposeWebContainerContextAndFilter() throws InterruptedException, IOException {
        Reader script = new StringReader("" +
                "package org.nanocontainer.script.groovy\n" +
                "builder = new GroovyNodeBuilder()\n" +
                "nano = builder.container {\n" +
                "    component(instance:'Fred')\n" +
                "    component(instance:new Integer(5))\n" +
                // declare the web container
                "    webContainer(port:8080) {\n" +
                "        context(path:'/bar') {\n" +
                "            filter(path:'/*', class:org.nanocontainer.webcontainer.DependencyInjectionTestFilter," +
                "                   dispatchers: new Integer(0)){\n" +
                "               initParam(name:'foo', value:'bau')\n" +
                "            }\n" +
                "            servlet(path:'/foo2', class:org.nanocontainer.webcontainer.DependencyInjectionTestServlet)\n" +

                "        }\n" +
                "    }\n" +
                // end declaration
                "}\n");

        assertPageIsHostedWithContents(script, "hello Fred Filtered!(int= 5 bau)", "http://localhost:8080/bar/foo2");
    }

    public void testCanComposeWebContainerContextAndServlet() throws InterruptedException, IOException {
        Reader script = new StringReader("" +
                "package org.nanocontainer.script.groovy\n" +
                "builder = new GroovyNodeBuilder()\n" +
                "nano = builder.container {\n" +
                "    component(instance:'Fred')\n" +
                // declare the web container
                "    webContainer(port:8080) {\n" +
                "        context(path:'/bar') {\n" +
                "            servlet(path:'/foo', class:org.nanocontainer.webcontainer.DependencyInjectionTestServlet){\n" +
                "               initParam(name:'foo', value:'bar')\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                // end declaration
                "}\n");

        assertPageIsHostedWithContents(script, "hello Fred bar", "http://localhost:8080/bar/foo");
    }

    public void testCanComposeWebContainerContextAndServletInstance() throws InterruptedException, IOException {
        Reader script = new StringReader("" +
                "package org.nanocontainer.script.groovy\n" +
                "builder = new GroovyNodeBuilder()\n" +
                "nano = builder.container {\n" +
                // declare the web container
                "    webContainer(port:8080) {\n" +
                "        context(path:'/bar') {\n" +
                "            servlet(path:'/foo', instance:new org.nanocontainer.webcontainer.DependencyInjectionTestServlet('Fred')) {\n" +
                //"                setFoo(foo:'bar')\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                // end declaration
                "}\n");

        assertPageIsHostedWithContents(script, "hello Fred", "http://localhost:8080/bar/foo");
    }

    
    public void testCanComposeWebContainerContextWithExplicitConnector() throws InterruptedException, IOException {
        Reader script = new StringReader("" +
                "package org.nanocontainer.script.groovy\n" +
                "builder = new GroovyNodeBuilder()\n" +
                "nano = builder.container {\n" +
                "    component(instance:'Fred')\n" +
                // declare the web container
                "    webContainer() {\n" +
                "        blockingChannelConnector(host:'localhost', port:8080)\n" +
                "        context(path:'/bar') {\n" +
                "            servlet(path:'/foo', class:org.nanocontainer.webcontainer.DependencyInjectionTestServlet)\n" +
                "        }\n" +
                "    }\n" +
                // end declaration
                "}\n");

        assertPageIsHostedWithContents(script, "hello Fred", "http://localhost:8080/bar/foo");
    }

    public void testCanComposeWebContainerAndWarFile() throws InterruptedException, IOException {

        File testWar = TestHelper.getTestWarFile();

        Reader script = new StringReader("" +
                "package org.nanocontainer.script.groovy\n" +
                "builder = new GroovyNodeBuilder()\n" +
                "nano = builder.container {\n" +
                "    component(instance:'Fred')\n" +
                "    component(instance:new Integer(5))\n" +
                "    component(key:StringBuffer.class, instance:new StringBuffer())\n" +
                // declare the web container
                "    webContainer() {\n" +
                "        blockingChannelConnector(host:'localhost', port:8080)\n" +
                "        xmlWebApplication(path:'/bar', warfile:'"+testWar.getAbsolutePath().replace('\\','/')+"')" +
                "    }\n" +
                // end declaration
                "}\n");

        assertPageIsHostedWithContents(script, "hello Fred bar", "http://localhost:8080/bar/foo");
        assertEquals("-contextInitialized", pico.getComponentInstance(StringBuffer.class).toString());
    }

    public void testCanComposeWebContainerContextAndListener() throws InterruptedException, IOException {

        Reader script = new StringReader("" +
                "package org.nanocontainer.script.groovy\n" +
                "builder = new GroovyNodeBuilder()\n" +
                "nano = builder.container {\n" +
                "    component(class:StringBuffer.class)\n" +
                // declare the web container
                "    webContainer(port:8080) {\n" +
                "        context(path:'/bar') {\n" +
                "            listener(class:org.nanocontainer.webcontainer.DependencyInjectionTestListener)\n" +
                "        }\n" +
                "    }\n" +
                // end declaration
                "}\n");

        assertPageIsHostedWithContents(script, "java.io.FileNotFoundException:http://localhost:8080/bar/", "http://localhost:8080/bar/");

        StringBuffer stringBuffer = (StringBuffer) pico.getComponentInstance(StringBuffer.class);

        assertEquals("-contextInitialized", stringBuffer.toString());

        pico.stop();
        pico = null;

        assertEquals("-contextInitialized-contextDestroyed", stringBuffer.toString());

    }

    public void testStaticContentCanBeServed() throws InterruptedException, IOException {

        File testWar = TestHelper.getTestWarFile();

		String absolutePath = testWar.getParentFile().getAbsolutePath();
		absolutePath = absolutePath.replace('\\', '/');
        
        Reader script = new StringReader("" +
                "package org.nanocontainer.script.groovy\n" +
                "builder = new GroovyNodeBuilder()\n" +
                "nano = builder.container {\n" +
                // declare the web container
                "    webContainer(port:8080) {\n" +
                "        context(path:'/bar') {\n" +
                "            staticContent(path:'"+absolutePath+"')\n" +
                "        }\n" +
                "    }\n" +
                // end declaration
                "}\n");

        assertPageIsHostedWithContents(script, "<html>\n" +
                " <body>\n" +
                "   hello\n" +
                " </body>\n" +
                "</html>", "http://localhost:8080/bar/hello.html");


        pico.stop();
        pico = null;


    }

    public void testStaticContentCanBeServedWithDefaultWelcomePage() throws InterruptedException, IOException {

        File testWar = TestHelper.getTestWarFile();

        String absolutePath = testWar.getParentFile().getAbsolutePath();
        absolutePath = absolutePath.replace('\\', '/');
        
        Reader script = new StringReader("" +
                "package org.nanocontainer.script.groovy\n" +
                "builder = new GroovyNodeBuilder()\n" +
                "nano = builder.container {\n" +
                // declare the web container
                "    webContainer(port:8080) {\n" +
                "        context(path:'/bar') {\n" +
                "            staticContent(path:'" + absolutePath + "', welcomePage:'hello.html')\n" +
                "        }\n" +
                "    }\n" +
                // end declaration
                "}\n");

        assertPageIsHostedWithContents(script, "<html>\n" +
                " <body>\n" +
                "   hello\n" +
                " </body>\n" +
                "</html>", "http://localhost:8080/bar/");


        pico.stop();
        pico = null;

    }

    private void assertPageIsHostedWithContents(Reader script, String message, String url) throws InterruptedException, IOException {
        pico = buildContainer(script, null, "SOME_SCOPE");
        assertNotNull(pico);
        
        String actual = getPage(url);
        assertEquals(message, actual);
    }

    private PicoContainer buildContainer(Reader script, PicoContainer parent, Object scope) {
        parentContainerRef.set(parent);
        new GroovyContainerBuilder(script, getClass().getClassLoader()).buildContainer(containerRef, parentContainerRef, scope, true);
        return (PicoContainer) containerRef.get();
    }
}
