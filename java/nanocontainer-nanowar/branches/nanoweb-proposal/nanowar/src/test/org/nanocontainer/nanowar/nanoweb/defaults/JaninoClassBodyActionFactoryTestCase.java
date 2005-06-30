package org.nanocontainer.nanowar.nanoweb.defaults;

import java.net.URI;

import junit.framework.TestCase;

import org.picocontainer.defaults.DefaultPicoContainer;

public class JaninoClassBodyActionFactoryTestCase extends TestCase {

	public void testBasic() throws Exception {

		DefaultPicoContainer emptyPico = new DefaultPicoContainer();

		JaninoClassBodyActionFactory factory = new JaninoClassBodyActionFactory(new URI(this.getClass().getResource("/").toString())
				.getPath(), "janino");

		Object action = factory.getInstance(emptyPico, "/org/nanocontainer/nanowar/nanoweb/defaults/dummy");
		assertNotNull(action);
		assertEquals("Ctor called", action.getClass().getField("assertCtor").get(action));
		assertEquals("Yes! Its me, Janino!", action.getClass().getMethod("execute", null).invoke(action, null));
	}

}
