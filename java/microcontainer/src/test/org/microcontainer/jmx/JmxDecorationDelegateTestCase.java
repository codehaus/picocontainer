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

import javax.management.MBeanServer;
import javax.management.MBeanInfo;
import javax.management.ObjectName;

/**
 * @author Michael Ward
 */
public class JmxDecorationDelegateTestCase extends TestCase {

	private ObjectReference containerRef = new SimpleReference();
	private ObjectReference parentContainerRef = new SimpleReference();

	public void testScript() throws Exception {

		Reader script = new StringReader("" +
				"import org.nanocontainer.script.groovy.NanoContainerBuilder\n" +
				"import org.microcontainer.jmx.JmxDecorationDelegate\n" +
				"\n" +
				"builder = new NanoContainerBuilder(new JmxDecorationDelegate())\n" +
				"pico = builder.container {\n" +
				"	component(key:javax.management.MBeanServer, instance:javax.management.MBeanServerFactory.newMBeanServer())\n" +
				"	jmx(key:'domain:wilma=default', operations:['helloCalled']) {\n" +
				"   	component(key:org.nanocontainer.testmodel.Wilma, class:org.nanocontainer.testmodel.WilmaImpl)\n" +
				"   }\n" +
				"}");

		PicoContainer pico = buildContainer(new GroovyContainerBuilder(script, getClass().getClassLoader()), null, "SOME_SCOPE");
		MBeanServer mBeanServer = (MBeanServer)pico.getComponentInstance(MBeanServer.class);

		ObjectName objectName = new ObjectName("domain:wilma=default");
		Wilma wilma = (Wilma)pico.getComponentInstance(objectName.getCanonicalName());
		assertEquals("Wilma should be registered to the object name and the key (Wilma interface)", wilma, pico.getComponentInstance(Wilma.class));
		wilma.hello();

		// MBeanInfo is registered to the implementation
		MBeanInfo mBeanInfo = (MBeanInfo)pico.getComponentInstance("org.nanocontainer.testmodel.WilmaImplMBeanInfo");
		assertNotNull(mBeanInfo);
		assertEquals("Only one operation should be defined in the MBeanInfo", 1, mBeanInfo.getOperations().length);

		Boolean called = (Boolean)mBeanServer.invoke(objectName, "helloCalled", null, null);
		assertTrue(called.booleanValue());
	}

	protected PicoContainer buildContainer(ScriptedContainerBuilder builder, PicoContainer parentContainer, Object scope) {
		parentContainerRef.set(parentContainer);
		builder.buildContainer(containerRef, parentContainerRef, scope, true);
		return (PicoContainer) containerRef.get();
	}
}
