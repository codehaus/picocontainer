package org.picocontainer.gems.parameters;

import java.io.File;
import java.util.List;

import junit.framework.TestCase;

import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.ParameterName;
import org.picocontainer.PicoContainer;

/**
 * test that config parameter does the right job
 * 
 * @author k.pribluda
 * 
 */
public class ConfigParameterTestCase extends TestCase {


	// defaultparameter name, just for convenience
	ParameterName paramName = new ParameterName() {
		public String getName() {
			return "gloo.blum";
		}

	};

	public void testThatNoEntryIsWorkingProperly() throws Exception {
		PicoContainer container = new DefaultPicoContainer();
		ConfigParameter parameter = new ConfigParameter("gloo.blum");

		// shall be not resolvable
		assertFalse(parameter.isResolvable(container, null, String.class,
				paramName, false));

		// shall resolve instance as null
		assertNull(parameter.resolveInstance(container, null, String.class,
				paramName, false));
	}

	public void testThatNotStringEntryIsNotResolved() throws Exception {
		MutablePicoContainer container = new DefaultPicoContainer();
		container.addComponent("gloo.blum", new Integer(239));

		ConfigParameter parameter = new ConfigParameter("gloo.blum");

		// shall be not resolvable
		assertFalse(parameter.isResolvable(container, null, String.class,
				paramName, false));

		// shall resolve instance as null
		assertNull(parameter.resolveInstance(container, null, String.class,
				paramName, false));

	}

	/**
	 * shall resolve as ddifferent classes
	 * 
	 * @throws Exception
	 */
	public void testThatResolvedSuccessfully() throws Exception {
		MutablePicoContainer container = new DefaultPicoContainer();
		container.addComponent("gloo.blum", "239");

		ConfigParameter parameter = new ConfigParameter("gloo.blum");

		assertEquals(new Integer(239), parameter.resolveInstance(container,
				null, Integer.class, paramName, false));
		assertEquals("239", parameter.resolveInstance(container, null,
				String.class, paramName, false));
	}

	/**
	 * shall bomb properly if no suitable converter found
	 * 
	 */
	public void testThatUnavailableConverterProducesCorrectException() {
		MutablePicoContainer container = new DefaultPicoContainer();
		container.addComponent("gloo.blum", "239");

		ConfigParameter parameter = new ConfigParameter("gloo.blum");

		try {
			parameter.resolveInstance(container, null, List.class, paramName,
					false);
			fail("failed to bomb on unavailable converter");
		} catch (ConfigParameter.NoConverterAvailableException ex) {
			// that's anticipated
		}
	}
	
	

	public void testComponentInstantiation() {
		DefaultPicoContainer properties = new DefaultPicoContainer();
		properties.addComponent("numericProperty", "239");
		properties.addComponent("doubleProperty", "17.95");
		properties.addComponent("stringProperty", "foo.bar");

		DefaultPicoContainer container = new DefaultPicoContainer(properties);
		container.addComponent("configured", ExternallyConfiguredComponent.class,
						new ConfigParameter("numericProperty"),
						// resolves as string
						new ConfigParameter("stringProperty"),
						// resolves as file
						new ConfigParameter("stringProperty"),
						// resolves as double
						new ConfigParameter("doubleProperty")
					
				);
		
		
		ExternallyConfiguredComponent component = (ExternallyConfiguredComponent) container.getComponent("configured");
		
		assertNotNull(component);
		assertEquals(239,component.getLongValue());
		assertEquals("foo.bar",component.getStringParameter());
		assertEquals(new File("foo.bar"),component.getFileParameter());
		assertEquals(17.95,component.getDoubleParameter(),0);
	}
	/**
	 * test component to show automatic conversion
	 * 
	 * @author ko5tik
	 */

	public static class ExternallyConfiguredComponent {
		long longValue;

		String stringParameter;

		File fileParameter;

		double doubleParameter;

		public ExternallyConfiguredComponent(long longValue, String stringParameter, File fileParameter, double doubleParameter) {
			super();
			this.longValue = longValue;
			this.stringParameter = stringParameter;
			this.fileParameter = fileParameter;
			this.doubleParameter = doubleParameter;
		}

		public double getDoubleParameter() {
			return doubleParameter;
		}

		public File getFileParameter() {
			return fileParameter;
		}

		public long getLongValue() {
			return longValue;
		}

		public String getStringParameter() {
			return stringParameter;
		}

		public void setDoubleParameter(double doubleParameter) {
			this.doubleParameter = doubleParameter;
		}

		public void setFileParameter(File fileParameter) {
			this.fileParameter = fileParameter;
		}

		public void setLongValue(long longValue) {
			this.longValue = longValue;
		}

		public void setStringParameter(String stringParameter) {
			this.stringParameter = stringParameter;
		}

	}

}
