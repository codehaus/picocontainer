package org.nanocontainer.nanowar.nanoweb.defaults;

import java.net.URI;

import junit.framework.TestCase;

import org.picocontainer.defaults.DefaultPicoContainer;

/**
 * @version $Revision: 1.1 $
 */
public class GroovyActionFactoryTestCase extends TestCase {

	public void testBasic() throws Exception {

		DefaultPicoContainer emptyPico = new DefaultPicoContainer();

		GroovyActionFactory factory = new GroovyActionFactory(new URI(this.getClass().getResource("/").toString()).getPath(), "groovy");

		Object action = factory.getInstance(emptyPico, "/org/nanocontainer/nanowar/nanoweb/defaults/dummy");
		assertNotNull(action);
		assertEquals("Ctor called", action.getClass().getField("assertCtor").get(action));
		assertEquals("Yes! its me, Groovy!", action.getClass().getMethod("execute", null).invoke(action, null));
	}
}
