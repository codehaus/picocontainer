package org.picocontainer.defaults;

import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.internals.ComponentFactory;
import org.picocontainer.internals.ComponentRegistry;
import org.picocontainer.internals.ComponentSpecification;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.testmodel.Touchable;

import junit.framework.TestCase;

public class DefaultComponentRegistryTestCase extends TestCase {
	private ComponentRegistry componentRegistry;
	private ComponentFactory componentFactory;

	protected void setUp() throws Exception {
		componentRegistry = new DefaultComponentRegistry();
		componentFactory = new DefaultComponentFactory();
	}
	
	public void testRegisterComponent() throws PicoIntrospectionException {
		ComponentSpecification componentSpecification =
			createComponentSpecification();
		
		componentRegistry.registerComponent(componentSpecification);
		
		assertTrue(componentRegistry.getComponentSpecifications().contains(
			componentSpecification));
	}
	
	public void testUnregisterComponent() throws PicoIntrospectionException {
		ComponentSpecification componentSpecification =
			createComponentSpecification();
			
		componentRegistry.registerComponent(componentSpecification);
		
		componentRegistry.unregisterComponent(Touchable.class);
		
		assertFalse(componentRegistry.getComponentSpecifications().contains(
			componentSpecification));
	}

	private ComponentSpecification createComponentSpecification() throws PicoIntrospectionException {
		return new ComponentSpecification(componentFactory, Touchable.class,
			SimpleTouchable.class);
	}
}
