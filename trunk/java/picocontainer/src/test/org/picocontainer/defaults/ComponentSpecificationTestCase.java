package org.picocontainer.defaults;

import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.internals.ComponentSpecification;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.testmodel.Touchable;

import junit.framework.TestCase;

public class ComponentSpecificationTestCase extends TestCase {
	public void testEquals() throws PicoIntrospectionException {
		ComponentSpecification componentSpecification =
			createComponentSpecification();
		
		assertEquals(componentSpecification, componentSpecification);
	}
	
	private ComponentSpecification createComponentSpecification() throws PicoIntrospectionException {
		return new ComponentSpecification(new DefaultComponentFactory(), Touchable.class,
			SimpleTouchable.class);
	}
}
