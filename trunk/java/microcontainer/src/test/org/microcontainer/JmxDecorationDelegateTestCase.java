package org.microcontainer;

import junit.framework.TestCase;

import java.io.Reader;
import java.io.StringReader;
import java.util.Map;

import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.ObjectReference;
import org.picocontainer.defaults.SimpleReference;
import org.nanocontainer.script.groovy.GroovyContainerBuilder;
import org.nanocontainer.script.ScriptedContainerBuilder;

import javax.management.MBeanServer;
import javax.management.MBeanInfo;
import javax.management.ObjectName;

/**
 * @author Michael Ward
 */
public class JmxDecorationDelegateTestCase extends TestCase {

	private ObjectReference containerRef = new SimpleReference();
	private ObjectReference parentContainerRef = new SimpleReference();

	public void testIt() throws Exception {

		Reader script = new StringReader("" +
				"builder = new org.microcontainer.MicroGroovyBuilder()\n" +
				"pico = builder.container(parent:parent) {\n" +
				"	component(key:javax.management.MBeanServer, instance:javax.management.MBeanServerFactory.newMBeanServer())\n" +
				"	jmx(key:'microcontainer:kernel=default', methods:['size']) {\n" +
				"   	component(key:java.util.Map, class:java.util.HashMap)\n" +
				"   }\n" +
				"}");

		PicoContainer pico = buildContainer(new GroovyContainerBuilder(script, getClass().getClassLoader()), null, "SOME_SCOPE");
		MBeanServer mBeanServer = (MBeanServer)pico.getComponentInstance(MBeanServer.class);

		ObjectName objectName = new ObjectName("microcontainer:kernel=default");
		Map map = (Map)pico.getComponentInstance(objectName.getCanonicalName());
		map.put("hello", "world");
		map.put("foo", "bar");

		// MBeanInfo is registered to the implementation
		MBeanInfo mBeanInfo = (MBeanInfo)pico.getComponentInstance("java.util.HashMapMBeanInfo");
		assertNotNull(mBeanInfo);

		Integer size = (Integer)mBeanServer.invoke(objectName, "size", null, null);
		assertEquals(2, size.intValue());
	}

	protected PicoContainer buildContainer(ScriptedContainerBuilder builder, PicoContainer parentContainer, Object scope) {
		parentContainerRef.set(parentContainer);
		builder.buildContainer(containerRef, parentContainerRef, scope, true);
		return (PicoContainer) containerRef.get();
	}
}
