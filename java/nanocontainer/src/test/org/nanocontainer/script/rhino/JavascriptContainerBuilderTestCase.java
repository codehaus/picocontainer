package org.picoextras.script.rhino;

import org.mozilla.javascript.JavaScriptException;
import org.picocontainer.PicoContainer;
import org.picoextras.integrationkit.PicoAssemblyException;
import org.picoextras.script.AbstractScriptedAssemblingLifecycleContainerBuilderTestCase;
import org.picoextras.testmodel.WebServer;
import org.picoextras.testmodel.WebServerConfig;
import org.picoextras.testmodel.WebServerImpl;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Proxy;

public class JavascriptContainerBuilderTestCase extends AbstractScriptedAssemblingLifecycleContainerBuilderTestCase {

    public void testInstantiateBasicScriptable() throws IOException, ClassNotFoundException, PicoAssemblyException, JavaScriptException {

        Reader script = new StringReader("" +
                "var pico = new DefaultPicoContainer()\n" +
                "pico.registerComponentImplementation(Packages.org.picoextras.testmodel.DefaultWebServerConfig)\n");

        PicoContainer pico = buildContainer(new JavascriptContainerBuilder(script, getClass().getClassLoader()));

        assertNotNull(pico.getComponentInstanceOfType(WebServerConfig.class).getClass());
    }

    public void testInstantiateWithBespokeComponentAdapter() throws IOException, ClassNotFoundException, PicoAssemblyException, JavaScriptException {

        Reader script = new StringReader("" +
                "var pico = new DefaultPicoContainer(new ImplementationHidingComponentAdapterFactory())\n" +
                "pico.registerComponentImplementation(Packages.org.picoextras.testmodel.DefaultWebServerConfig)\n" +
                "pico.registerComponentImplementation(Packages.org.picoextras.testmodel.WebServerImpl)\n");

        PicoContainer pico = buildContainer(new JavascriptContainerBuilder(script, getClass().getClassLoader()));

        Object wsc = pico.getComponentInstanceOfType(WebServerConfig.class);
        Object ws = pico.getComponentInstanceOfType(WebServer.class);

        assertTrue(ws instanceof WebServer);
        assertFalse(ws instanceof WebServerImpl);
        assertTrue(Proxy.isProxyClass(ws.getClass()));

        assertEquals("ClassLoader should be the same for both components", ws.getClass().getClassLoader(), wsc.getClass().getClassLoader());
    }

    public void testClassLoaderHierarchy() throws ClassNotFoundException, IOException, PicoAssemblyException, JavaScriptException {

        String testcompJarFileName = System.getProperty("testcomp.jar");
        // Paul's path to TestComp. PLEASE do not take out.
        //testcompJarFileName = "D:/DEV/nano/reflection/src/test-comp/TestComp.jar";

        assertNotNull("The testcomp.jar system property should point to TestComp.jar", testcompJarFileName);
        File testCompJar = new File(testcompJarFileName);
        assertTrue(testCompJar.isFile());

        final String testCompJarPath = testCompJar.getCanonicalPath().replace('\\', '/');
        Reader script = new StringReader(
                "var pico = new DefaultPicoContainer()\n" +
                "pico.registerComponentImplementation('parentComponent', Packages." + FooTestComp.class.getName() + ")\n" +
                "childAdapter = new DefaultReflectionContainerAdapter(new DefaultPicoContainer(pico))\n" +
                "url = new File('" + testCompJarPath + "').toURL()\n" +
                "childAdapter.addClassLoaderURL(url)\n" +
                "childAdapter.registerComponentImplementation('childComponent','TestComp')\n" +
                "pico.registerComponentInstance('child1', childAdapter.getPicoContainer())");
        PicoContainer pico = buildContainer(new JavascriptContainerBuilder(script, getClass().getClassLoader()));

        Object parentComponent = pico.getComponentInstance("parentComponent");

        PicoContainer childContainer = (PicoContainer) pico.getComponentInstance("child1");
        Object childComponent = childContainer.getComponentInstance("childComponent");

        assertNotSame(parentComponent.getClass().getClassLoader(), childComponent.getClass().getClassLoader());
        /*
        system cl -> loads FooTestComp
          parent container cl
            child container cl -> loads TestComp
        */
        assertSame(parentComponent.getClass().getClassLoader(), childComponent.getClass().getClassLoader().getParent());
    }

    public void testRegisterComponentInstance() throws JavaScriptException, IOException {
        Reader script = new StringReader("" +
                "var pico = new DefaultPicoContainer(new ImplementationHidingComponentAdapterFactory())\n" +
                "pico.registerComponentInstance( new Packages." + FooTestComp.class.getName() + "())\n" +
                "pico.registerComponentInstance( 'foo', new Packages." + FooTestComp.class.getName() + "())\n");

        PicoContainer pico = buildContainer(new JavascriptContainerBuilder(script, getClass().getClassLoader()));

        assertEquals(FooTestComp.class, pico.getComponentInstances().get(0).getClass());
        assertEquals(FooTestComp.class, pico.getComponentInstances().get(1).getClass());
    }

    public static class FooTestComp {

    }

}
