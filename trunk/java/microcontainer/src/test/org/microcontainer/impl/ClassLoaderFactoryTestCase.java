package org.microcontainer.impl;

import junit.framework.TestCase;

import java.io.File;

/**
 * @author Mike Ward
 */
public class ClassLoaderFactoryTestCase extends TestCase {

    public void testShouldKeepJunitHappy() {
    }

	public void FIXME_and_please_use_a_method_name_that_describes_the_intent_of_the_test() throws Exception {
		ClassLoaderFactory clf = new ClassLoaderFactory(new DefaultMcaDeployer());
        new File("test").mkdir();
		ClassLoader classLoader = clf.build("test");
		assertNotNull(classLoader);

		// from Components
		Class testCompClass = classLoader.loadClass("org.microcontainer.test.TestComp");
		assertEquals("org.microcontainer.test.TestComp", testCompClass.getName());

		// Ensure we can access classes from parent class loader
		Class groovyContainerBuilderClass = classLoader.loadClass("org.nanocontainer.script.groovy.GroovyContainerBuilder");
		assertEquals("org.nanocontainer.script.groovy.GroovyContainerBuilder", groovyContainerBuilderClass.getName());
	}
}
