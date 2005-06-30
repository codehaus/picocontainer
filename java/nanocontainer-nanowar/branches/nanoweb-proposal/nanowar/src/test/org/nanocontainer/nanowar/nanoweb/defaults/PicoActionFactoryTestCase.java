package org.nanocontainer.nanowar.nanoweb.defaults;

import junit.framework.TestCase;

import org.nanocontainer.nanowar.nanoweb.MyAction;
import org.picocontainer.defaults.DefaultPicoContainer;

public class PicoActionFactoryTestCase extends TestCase {

	public void testBasic() throws Exception {
		DefaultPicoContainer pico = new DefaultPicoContainer();
		pico.registerComponentImplementation("/it/should/exist", MyAction.class);

		PicoActionFactory factory = new PicoActionFactory();

		assertNull(factory.getInstance(pico, "/it/should/not/exist"));
		assertNotNull(factory.getInstance(pico, "/it/should/exist"));
	}

}
