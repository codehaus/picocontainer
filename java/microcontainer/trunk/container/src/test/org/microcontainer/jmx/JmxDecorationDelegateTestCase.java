package org.microcontainer.jmx;

import junit.framework.TestCase;

import java.io.Reader;
import java.io.StringReader;

import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.ObjectReference;
import org.picocontainer.defaults.SimpleReference;
import org.nanocontainer.script.groovy.GroovyContainerBuilder;
import org.nanocontainer.script.ScriptedContainerBuilder;
import org.nanocontainer.testmodel.Wilma;
import org.nanocontainer.testmodel.WilmaImpl;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.MBeanInfo;

/**
 * @author Michael Ward
 * @author J&ouml;rg Schaible
 */
public class JmxDecorationDelegateTestCase extends TestCase {

	private ObjectReference containerRef = new SimpleReference();
	private ObjectReference parentContainerRef = new SimpleReference();

	public void testScriptWithJmxNodeAndKeyAsManagementInterface() throws Exception {

		Reader script = new StringReader("" +
				"import org.nanocontainer.script.groovy.NanoContainerBuilder\n" +
				"import org.microcontainer.jmx.JmxDecorationDelegate\n" +
				"\n" +
				"builder = new NanoContainerBuilder(new JmxDecorationDelegate())\n" +
				"pico = builder.container() {\n" +
				"	component(key:javax.management.MBeanServer, instance:javax.management.MBeanServerFactory.createMBeanServer())\n" +
				" 	component(key:org.nanocontainer.testmodel.Wilma, class:org.nanocontainer.testmodel.WilmaImpl) {\n" +
				"		jmx(key:'domain:wilma=default', operations:['helloCalled'], description:'jmx description text')\n" +
				"   }\n" +
				"}");

		PicoContainer pico = buildContainer(new GroovyContainerBuilder(script, getClass().getClassLoader()), null, "SOME_SCOPE");
		MBeanServer mBeanServer = (MBeanServer)pico.getComponentInstance(MBeanServer.class);

		ObjectName objectName = new ObjectName("domain:wilma=default");

		MBeanInfo mBeanInfo = mBeanServer.getMBeanInfo(objectName);
		assertEquals("jmx description text", mBeanInfo.getDescription());
		assertEquals(1, mBeanInfo.getOperations().length);

		Wilma wilma = (Wilma)pico.getComponentInstance(Wilma.class);
		wilma.hello();

		Boolean called = (Boolean)mBeanServer.invoke(objectName, "helloCalled", null, null);
		assertTrue(called.booleanValue());
	}

    public void testScriptWithJmxNodeAndExplicitManagementInterface() throws Exception {

        Reader script = new StringReader("" +
                "import org.nanocontainer.script.groovy.NanoContainerBuilder\n" +
                "import org.microcontainer.jmx.JmxDecorationDelegate\n" +
                "\n" +
                "builder = new NanoContainerBuilder(new JmxDecorationDelegate())\n" +
                "pico = builder.container() {\n" +
                "   component(key:javax.management.MBeanServer, instance:javax.management.MBeanServerFactory.createMBeanServer())\n" +
                "   component(key:'wilma', class:org.nanocontainer.testmodel.WilmaImpl) {\n" +
                "       jmx(key:'domain:wilma=default', management:org.nanocontainer.testmodel.Wilma, operations:['helloCalled'], description:'jmx description text')\n" +
                "   }\n" +
                "}");

        PicoContainer pico = buildContainer(new GroovyContainerBuilder(script, getClass().getClassLoader()), null, "SOME_SCOPE");
        MBeanServer mBeanServer = (MBeanServer)pico.getComponentInstance(MBeanServer.class);

        ObjectName objectName = new ObjectName("domain:wilma=default");

        MBeanInfo mBeanInfo = mBeanServer.getMBeanInfo(objectName);
        assertEquals("jmx description text", mBeanInfo.getDescription());
        assertEquals(1, mBeanInfo.getOperations().length);

        Wilma wilma = (Wilma)pico.getComponentInstance("wilma");
        wilma.hello();

        Boolean called = (Boolean)mBeanServer.invoke(objectName, "helloCalled", null, null);
        assertTrue(called.booleanValue());
    }

    public static interface WilmaFlintstoneMBean extends Wilma {
    }

    public static class WilmaFlintstone extends WilmaImpl implements WilmaFlintstoneMBean {
    }

    public void testScriptWithJmxNodeAndImplicitManagementInterface() throws Exception {

        Reader script = new StringReader("" +
                "import org.nanocontainer.script.groovy.NanoContainerBuilder\n" +
                "import org.microcontainer.jmx.JmxDecorationDelegate\n" +
                "\n" +
                "builder = new NanoContainerBuilder(new JmxDecorationDelegate())\n" +
                "pico = builder.container() {\n" +
                "   component(key:javax.management.MBeanServer, instance:javax.management.MBeanServerFactory.createMBeanServer())\n" +
                "   component(key:'wilma', class:org.microcontainer.jmx.JmxDecorationDelegateTestCase$WilmaFlintstone) {\n" +
                "       jmx(key:'domain:wilma=default', operations:['helloCalled'], description:'jmx description text')\n" +
                "   }\n" +
                "}");

        PicoContainer pico = buildContainer(new GroovyContainerBuilder(script, getClass().getClassLoader()), null, "SOME_SCOPE");
        MBeanServer mBeanServer = (MBeanServer)pico.getComponentInstance(MBeanServer.class);

        ObjectName objectName = new ObjectName("domain:wilma=default");

        MBeanInfo mBeanInfo = mBeanServer.getMBeanInfo(objectName);
        assertEquals("jmx description text", mBeanInfo.getDescription());
        assertEquals(1, mBeanInfo.getOperations().length);

        Wilma wilma = (Wilma)pico.getComponentInstance("wilma");
        assertTrue(wilma instanceof WilmaFlintstone);
        wilma.hello();

        Boolean called = (Boolean)mBeanServer.invoke(objectName, "helloCalled", null, null);
        assertTrue(called.booleanValue());
    }

    public void testScriptWithJmxNodeAndWithoutManagementInterfaceUsingMX4J() throws Exception {

        final Package mx4j = Package.getPackage("mx4j");
        if (mx4j == null) {
            System.out.println("MX4J not available, aborting unit test 'testScriptWithJmxNodeAndWithoutManagementInterfaceUsingMX4J'");
            return;
        }
        Reader script = new StringReader("" +
                "import org.nanocontainer.script.groovy.NanoContainerBuilder\n" +
                "import org.microcontainer.jmx.JmxDecorationDelegate\n" +
                "\n" +
                "builder = new NanoContainerBuilder(new JmxDecorationDelegate())\n" +
                "pico = builder.container() {\n" +
                "   component(key:javax.management.MBeanServer, instance:javax.management.MBeanServerFactory.createMBeanServer())\n" +
                "   component(key:org.nanocontainer.remoting.jmx.DynamicMBeanFactory, class:'org.nanocontainer.remoting.jmx.mx4j.MX4JDynamicMBeanFactory')\n" +
                "   component(key:'wilma', class:org.nanocontainer.testmodel.WilmaImpl) {\n" +
                "       jmx(key:'domain:wilma=default', operations:['helloCalled'], description:'jmx description text')\n" +
                "   }\n" +
                "}");

        PicoContainer pico = buildContainer(new GroovyContainerBuilder(script, getClass().getClassLoader()), null, "SOME_SCOPE");
        MBeanServer mBeanServer = (MBeanServer)pico.getComponentInstance(MBeanServer.class);

        ObjectName objectName = new ObjectName("domain:wilma=default");

        MBeanInfo mBeanInfo = mBeanServer.getMBeanInfo(objectName);
        assertEquals("jmx description text", mBeanInfo.getDescription());
        assertEquals(1, mBeanInfo.getOperations().length);

        Wilma wilma = (Wilma)pico.getComponentInstance("wilma");
        wilma.hello();

        Boolean called = (Boolean)mBeanServer.invoke(objectName, "helloCalled", null, null);
        assertTrue(called.booleanValue());
    }

    public void testScriptWithJmxNodeAndWithoutManagementInterfaceUsingModelMBeans() throws Exception {

        Reader script = new StringReader("" +
                "import org.nanocontainer.script.groovy.NanoContainerBuilder\n" +
                "import org.microcontainer.jmx.JmxDecorationDelegate\n" +
                "\n" +
                "builder = new NanoContainerBuilder(new JmxDecorationDelegate())\n" +
                "pico = builder.container() {\n" +
                "   component(key:javax.management.MBeanServer, instance:javax.management.MBeanServerFactory.createMBeanServer())\n" +
                "   component(key:'wilma', class:org.nanocontainer.testmodel.WilmaImpl) {\n" +
                "       jmx(key:'domain:wilma=default', operations:['helloCalled'], description:'jmx description text')\n" +
                "   }\n" +
                "}");

        PicoContainer pico = buildContainer(new GroovyContainerBuilder(script, getClass().getClassLoader()), null, "SOME_SCOPE");
        MBeanServer mBeanServer = (MBeanServer)pico.getComponentInstance(MBeanServer.class);

        ObjectName objectName = new ObjectName("domain:wilma=default");

        MBeanInfo mBeanInfo = mBeanServer.getMBeanInfo(objectName);
        assertEquals("jmx description text", mBeanInfo.getDescription());
        assertEquals(1, mBeanInfo.getOperations().length);

        Wilma wilma = (Wilma)pico.getComponentInstance("wilma");
        wilma.hello();

        Boolean called = (Boolean)mBeanServer.invoke(objectName, "helloCalled", null, null);
        assertTrue(called.booleanValue());
    }

	protected PicoContainer buildContainer(ScriptedContainerBuilder builder, PicoContainer parentContainer, Object scope) {
		parentContainerRef.set(parentContainer);
		builder.buildContainer(containerRef, parentContainerRef, scope, true);
		return (PicoContainer) containerRef.get();
	}
}
