package org.microcontainer.impl;

import junit.framework.TestCase;
import java.io.File;
import org.microcontainer.ClassLoaderFactory;

/**
 * @author Mike Ward
 */
public class ClassLoaderFactoryTestCase extends TestCase {

	private ClassLoader classLoader;

	protected void setUp() throws Exception {
		ClassLoaderFactory clf = TestFixture.createClassLoaderFactory();
        new File("test").mkdir();
		classLoader = clf.build("test");
	}

	public void testLoadClassFromComponentPath() throws Exception {
		String className = "org.microcontainer.test.TestComp";
		Class clazz = classLoader.loadClass(className);
		assertEquals(className, clazz.getName());
		assertTrue(clazz.getClassLoader() instanceof StandardMicroClassLoader);
	}

	public void testLoadClassThatShouldBeHidden() {
		try {
			classLoader.loadClass("org.microcontainer.test.hopefullyhidden.TestCompImpl");
			fail("ClassNotFoundException should have been thrown.");
		} catch (ClassNotFoundException ignore) {
            // ignore
		}
	}

	/**
	 * Ensure we can access classes from parent class loader
	 */
	public void testLoadClassFromParent() throws Exception {
		Class clazz = classLoader.loadClass("org.nanocontainer.script.groovy.GroovyContainerBuilder");
		assertEquals("org.nanocontainer.script.groovy.GroovyContainerBuilder", clazz.getName());
	}

	public void testLoadClassFromPromoted() throws Exception {
		String className = "org.microcontainer.testapi.TestPromotable";
		Class clazz = classLoader.loadClass(className);
		assertEquals(className, clazz.getName());

		assertNotSame(classLoader, clazz.getClassLoader());
	}
}
