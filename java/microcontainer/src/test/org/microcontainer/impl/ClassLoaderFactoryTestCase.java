package org.megacontainer.impl;

import junit.framework.TestCase;

/**
 * @author Mike Ward
 */
public class ClassLoaderFactoryTestCase extends TestCase {

	public void testIt() throws Exception {
		ClassLoaderFactory clf = new ClassLoaderFactory();
		ClassLoader classLoader = clf.build("test");
		assertNotNull(classLoader);

		// from Components
		Class testCompClass = classLoader.loadClass("org.megacontainer.test.TestComp");
		assertEquals("org.megacontainer.test.TestComp", testCompClass.getName());

		// from Hidden
		Class testCompImplClass = classLoader.loadClass("org.megacontainer.test.hopefullyhidden.TestCompImpl");
		assertEquals("org.megacontainer.test.hopefullyhidden.TestCompImpl", testCompImplClass.getName());
		testCompImplClass.newInstance();

		// Ensure we can access classes from parent class loader
		Class groovyContainerBuilderClass = classLoader.loadClass("org.nanocontainer.script.groovy.GroovyContainerBuilder");
		assertEquals("org.nanocontainer.script.groovy.GroovyContainerBuilder", groovyContainerBuilderClass.getName());
	}
}
