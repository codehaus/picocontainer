package org.nanocontainer.rhino;

import junit.framework.TestCase;
import org.nanocontainer.testmodel.WebServer;
import org.nanocontainer.testmodel.WebServerImpl;
import org.nanocontainer.testmodel.WebServerConfig;
import org.picocontainer.PicoConfigurationException;
import org.picocontainer.PicoContainer;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Vector;
import java.util.Collection;

public class DefaultNanoRhinoScriptableTestCase extends TestCase {

    public void testInstantiateBasicRhinoScriptable() throws IOException, ClassNotFoundException, PicoConfigurationException {

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

    public void testInstantiateWithBespokeComponentAdaptor() throws IOException, ClassNotFoundException, PicoConfigurationException {

        Reader script = new StringReader("" +
                "var caf = new Packages.org.picocontainer.extras.ImplementationHidingComponentAdapterFactory();\n" +
                "var parentContainer = new NanoRhinoScriptable(caf);\n" +
                "with (parentContainer) {\n" +
                "  addComponentWithClassKey('org.nanocontainer.testmodel.WebServerConfig','org.nanocontainer.testmodel.DefaultWebServerConfig');\n" +
                "  addComponentWithClassKey('org.nanocontainer.testmodel.WebServer','org.nanocontainer.testmodel.WebServerImpl');\n" +
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

        //TODO - should be assertFalse( ), we're implementation hiding here !
        assertTrue(ws instanceof WebServerImpl);

        assertEquals("ClassLoader should be the same for both components",ws.getClass().getClassLoader(),wsc.getClass().getClassLoader());
    }

    public void testClassLoaderHierarchy() throws ClassNotFoundException, IOException, PicoConfigurationException {

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
