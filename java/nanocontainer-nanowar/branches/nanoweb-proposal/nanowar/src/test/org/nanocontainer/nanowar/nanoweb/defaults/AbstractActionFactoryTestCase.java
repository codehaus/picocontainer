package org.nanocontainer.nanowar.nanoweb.defaults;

import junit.framework.TestCase;

import org.nanocontainer.nanowar.nanoweb.MyAction;
import org.nanocontainer.nanowar.nanoweb.ScriptException;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;

public class AbstractActionFactoryTestCase extends TestCase {

	public void testBasic() throws Exception {
		PicoContainer pico = new DefaultPicoContainer();

		TestingAbstractActionFactory factory = new TestingAbstractActionFactory();

		factory.klass = MyAction.class;
		assertTrue(factory.getInstance(pico, "foo") instanceof MyAction);

		factory.klass = null;
		assertNull(factory.getInstance(pico, "foo"));
	}

	class TestingAbstractActionFactory extends AbstractActionFactory {

		public Class klass;

		protected Class getClass(String path) throws ScriptException {
			assertEquals("foo", path);
			return klass;
		}

	}

}
