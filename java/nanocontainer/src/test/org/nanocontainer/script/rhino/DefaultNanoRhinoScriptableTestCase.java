package org.picoextras.script.rhino;

import junit.framework.TestCase;
import org.picoextras.script.PicoCompositionException;
import org.picocontainer.PicoContainer;
import org.picoextras.testmodel.WebServer;
import org.picoextras.testmodel.WebServerConfig;
import org.picoextras.testmodel.WebServerImpl;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collection;

public class DefaultNanoRhinoScriptableTestCase extends TestCase {

    public void testInstantiateBasicRhinoScriptable() throws IOException, ClassNotFoundException, PicoCompositionException {

        Reader script = new StringReader("" +
                "var parentContainer = new NanoRhinoScriptable();\n" +
                "with (parentContainer) {\n" +
                "  addComponent('"+ FooTestComp.class.getName() +"');\n" +
                "}\n" +
                "nano.setNanoRhinoScriptable(parentContainer)\n"
        );

        PicoContainer rootContainer = new NanoRhinoManager().execute(DefaultNanoRhinoScriptable.class, script);

        assertEquals("Instantiated Object should be a FooTestComp", FooTestComp.class,
                rootContainer.getComponentInstances().get(0).getClass());
    }

    public void testInstantiateWithBespokeComponentAdapter() throws IOException, ClassNotFoundException, PicoCompositionException {

        Reader script = new StringReader("" +
                "var caf = new Packages.org.picocontainer.extras.ImplementationHidingComponentAdapterFactory();\n" +
                "var parentContainer = new NanoRhinoScriptable(caf);\n" +
                "with (parentContainer) {\n" +
                "  addComponentWithClassKey('org.picoextras.testmodel.WebServerConfig','org.picoextras.testmodel.DefaultWebServerConfig');\n" +
                "  addComponentWithClassKey('org.picoextras.testmodel.WebServer','org.picoextras.testmodel.WebServerImpl');\n" +
                "}\n" +
                "nano.setNanoRhinoScriptable(parentContainer)\n"
        );

        PicoContainer rootContainer = new NanoRhinoManager().execute(DefaultNanoRhinoScriptable.class, script);

        Object wsc = rootContainer.getComponentInstance(WebServerConfig.class);
        Object ws = rootContainer.getComponentInstance(WebServer.class);

        assertTrue(ws instanceof WebServer);
        assertFalse(ws instanceof WebServerImpl);

        ws = rootContainer.getComponentInstances().get(1);

        assertTrue(ws instanceof WebServer);

        assertFalse(ws instanceof WebServerImpl);

        assertEquals("ClassLoader should be the same for both components",ws.getClass().getClassLoader(),wsc.getClass().getClassLoader());
    }

    public void testClassLoaderHierarchy() throws ClassNotFoundException, IOException, PicoCompositionException {

        String testcompJarFileName = System.getProperty("testcomp.jar");
        // Paul's path to TestComp. PLEASE do not take out.
        //testcompJarFileName = "D:/DEV/nano/reflection/src/test-comp/TestComp.jar";

        assertNotNull("The testcomp.jar system property should point to nano/reflection/src/test-comp/TestComp.jar", testcompJarFileName);
        File testCompJar = new File(testcompJarFileName);
        assertTrue(testCompJar.isFile());
        String canonicalPath = testCompJar.getCanonicalPath();
        canonicalPath = canonicalPath.replace('\\','/');

        Reader script = new StringReader(
                "var parentContainer = new NanoRhinoScriptable();\n" +
                "with (parentContainer) {\n" +
                "  addComponent('foo','" + FooTestComp.class.getName() + "');\n" +
                "  var childContainer = new NanoRhinoScriptable();\n" +
                "  addContainer(childContainer);\n" +
                "  with (childContainer) {\n" +
                "    addFileClassPathJar(\"" + canonicalPath + "\");\n" +
                "    addComponent('bar','TestComp');\n" +
                "  }\n" +
                "}\n" +
                "nano.setNanoRhinoScriptable(parentContainer)\n");

        PicoContainer rootContainer = new NanoRhinoManager().execute(DefaultNanoRhinoScriptable.class, script);

        Object fooTestComp = rootContainer.getComponentInstance("foo");
        assertNotNull("Container should have a 'foo' component", fooTestComp);

        Collection childContainers = rootContainer.getChildContainers();
        PicoContainer childContainer = (PicoContainer) childContainers.iterator().next();
        Object barTestComp = childContainer.getComponentInstance("bar");
        assertNotNull("Container should have a 'bar' component", barTestComp);

        assertFalse("ClassLoader should not be the same for both components",fooTestComp.getClass().getClassLoader() == barTestComp.getClass().getClassLoader());

        assertTrue("ClassLoader for FooTestComp should not the parent of Bar's",fooTestComp.getClass().getClassLoader() == barTestComp.getClass().getClassLoader().getParent());


    }

    public static class FooTestComp {

    }

}
