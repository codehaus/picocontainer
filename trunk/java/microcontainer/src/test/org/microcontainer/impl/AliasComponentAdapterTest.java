package org.microcontainer.impl;

import junit.framework.TestCase;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.InstanceComponentAdapter;

/**
 * @author Michael Ward
 */
public class AliasComponentAdapterTest extends TestCase {

	private String DEFAULT_KEY = "KEY";

	public void testComponentAdapterMappedToAlias() throws Exception {
		MutablePicoContainer pico = new DefaultPicoContainer();

		// Build component adapter as normal
		ComponentAdapter componentAdapter = new InstanceComponentAdapter(DEFAULT_KEY, new Integer(5));
		pico.registerComponent(componentAdapter);

		// Build alias
		AliasComponentAdapter aliasComponentAdapter = new AliasComponentAdapter(Number.class, componentAdapter);
		pico.registerComponent(aliasComponentAdapter);

		Number numberOne = (Number)pico.getComponentInstance(DEFAULT_KEY);
		Number numberTwo = (Number)pico.getComponentInstance(Number.class);

		assertEquals("The same instance should have been returned", numberOne, numberTwo);
	}
}
