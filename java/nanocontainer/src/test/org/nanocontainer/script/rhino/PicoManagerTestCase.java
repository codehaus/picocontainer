package org.picoextras.script.rhino;

import junit.framework.TestCase;
import org.mozilla.javascript.JavaScriptException;
import org.picocontainer.PicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.extras.ImplementationHidingComponentAdapterFactory;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picoextras.script.PicoCompositionException;
import org.picoextras.testmodel.WebServer;
import org.picoextras.testmodel.WebServerConfig;
import org.picoextras.testmodel.WebServerImpl;
import org.picoextras.integrationkit.ContainerAssembler;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Proxy;

public class PicoManagerTestCase extends TestCase {

    private PicoContainer assemble(Reader script) {
        MutablePicoContainer pico = new DefaultPicoContainer();
        ContainerAssembler assembler = new JavascriptContainerAssembler(script);
        assembler.assembleContainer(pico, null);
        return pico;
    }

    public void testInstantiateBasicScriptable() throws IOException, ClassNotFoundException, PicoCompositionException, JavaScriptException {

        Reader script = new StringReader("" +
                "var parentContainer = new PicoScriptable();\n" +
                "with (parentContainer) {\n" +
                "  registerComponentImplementation('" + FooTestComp.class.getName() + "');\n" +
                "}\n"
        );

        PicoContainer pico = assemble(script);

        assertEquals("Instantiated Object should be a FooTestComp", FooTestComp.class,
                pico.getComponentInstances().get(0).getClass());
    }

    public void testInstantiateWithBespokeComponentAdapter() throws IOException, ClassNotFoundException, PicoCompositionException, JavaScriptException {

        Reader script = new StringReader("" +
                "var parentContainer = new PicoScriptable();\n" +
                "with (parentContainer) {\n" +
                "  registerComponentImplementation('org.picoextras.testmodel.WebServerConfig','org.picoextras.testmodel.DefaultWebServerConfig');\n" +
                "  registerComponentImplementation('org.picoextras.testmodel.WebServer','org.picoextras.testmodel.WebServerImpl');\n" +
                "}\n"
        );

        MutablePicoContainer pico = new DefaultPicoContainer(new ImplementationHidingComponentAdapterFactory());
        ContainerAssembler assembler = new JavascriptContainerAssembler(script);
        assembler.assembleContainer(pico, null);

        Object wsc = pico.getComponentInstance(WebServerConfig.class.getName());
        Object ws = pico.getComponentInstance(WebServer.class.getName());

        assertTrue(ws instanceof WebServer);
        assertFalse(ws instanceof WebServerImpl);
        assertTrue(Proxy.isProxyClass(ws.getClass()));

        assertEquals("ClassLoader should be the same for both components", ws.getClass().getClassLoader(), wsc.getClass().getClassLoader());
    }

    public void testClassLoaderHierarchy() throws ClassNotFoundException, IOException, PicoCompositionException, JavaScriptException {

        String testcompJarFileName = System.getProperty("testcomp.jar");
        // Paul's path to TestComp. PLEASE do not take out.
        //testcompJarFileName = "D:/DEV/nano/reflection/src/test-comp/TestComp.jar";

        assertNotNull("The testcomp.jar system property should point to TestComp.jar", testcompJarFileName);
        File testCompJar = new File(testcompJarFileName);
        assertTrue(testCompJar.isFile());
        String canonicalPath = testCompJar.getCanonicalPath();
        canonicalPath = canonicalPath.replace('\\', '/');

        Reader script = new StringReader(
                "var parentContainer = new PicoScriptable();\n" +
                "with (parentContainer) {\n" +
                "  registerComponentImplementation('parentComponent','" + FooTestComp.class.getName() + "');\n" +
                "  var childContainer = new PicoScriptable(parentContainer);\n" +
                "  with (childContainer) {\n" +
                "    addFileClassPathJar(\"" + canonicalPath + "\");\n" +
                "    registerComponentImplementation('childComponent','TestComp');\n" +
                "  }\n" +
                "  registerComponentInstance('child1', childContainer)" +
                "}\n");
        PicoContainer pico = assemble(script);

        Object parentComponent = pico.getComponentInstance("parentComponent");

        PicoContainer childContainer = (PicoContainer) pico.getComponentInstance("child1");
        Object childComponent = childContainer.getComponentInstance("childComponent");

        assertNotSame(parentComponent.getClass().getClassLoader(), childComponent.getClass().getClassLoader());
        /*
        system cl -> loads FooTestComp
          parent container cl
            child container cl -> loads TestComp
        */
        assertSame(parentComponent.getClass().getClassLoader(), childComponent.getClass().getClassLoader().getParent().getParent());
    }

    public void testRegisterComponentInstance() throws JavaScriptException, IOException {
        Reader script = new StringReader("" +
                "var picoS = new PicoScriptable();\n" +
                "with (picoS) {\n" +
                "  registerComponentInstance( new Packages." + FooTestComp.class.getName() + "());\n" +
                "  registerComponentInstance( 'foo', new Packages." + FooTestComp.class.getName() + "());\n" +
                "}\n"
        );

        PicoContainer pico = assemble(script);

        assertEquals(FooTestComp.class, pico.getComponentInstances().get(0).getClass());
        assertEquals(FooTestComp.class, pico.getComponentInstances().get(1).getClass());
    }

    public static class FooTestComp {

    }

}
