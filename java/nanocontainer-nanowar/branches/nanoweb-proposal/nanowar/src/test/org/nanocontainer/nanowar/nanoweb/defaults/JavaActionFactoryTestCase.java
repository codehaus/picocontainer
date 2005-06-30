package org.nanocontainer.nanowar.nanoweb.defaults;

import junit.framework.TestCase;

import org.nanocontainer.nanowar.nanoweb.MyAction;
import org.picocontainer.defaults.DefaultPicoContainer;

public class JavaActionFactoryTestCase extends TestCase {

	public void testWithNoBasePackage() throws Exception {
		DefaultPicoContainer emptyPico = new DefaultPicoContainer();

		JavaActionFactory factory = new JavaActionFactory();

		Object action = factory.getInstance(emptyPico, "/org/nanocontainer/nanowar/nanoweb/MyAction");
		assertNotNull(action);
		assertEquals(MyAction.class, action.getClass());
	}

	public void testWithBasePackageEndingWithADot() throws Exception {
		DefaultPicoContainer emptyPico = new DefaultPicoContainer();

		JavaActionFactory factory = new JavaActionFactory("org.nanocontainer.");

		Object action = factory.getInstance(emptyPico, "/nanowar/nanoweb/MyAction");
		assertNotNull(action);
		assertEquals(MyAction.class, action.getClass());
	}

	public void testWithBasePackageWithNoEndingDot() throws Exception {
		DefaultPicoContainer emptyPico = new DefaultPicoContainer();

		JavaActionFactory factory = new JavaActionFactory("org.nanocontainer");

		Object action = factory.getInstance(emptyPico, "/nanowar/nanoweb/MyAction");
		assertNotNull(action);
		assertEquals(MyAction.class, action.getClass());
	}

}
