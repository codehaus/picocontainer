package org.nanocontainer.jmx;

import junit.framework.TestCase;

import javax.management.ObjectName;
import javax.management.NotCompliantMBeanException;
import javax.management.StandardMBean;

import org.nanocontainer.testmodel.Wilma;
import org.nanocontainer.testmodel.WilmaImpl;

/**
 * @author Michael Ward
 * @version $Revision$
 */
public class StandardMBeanFactoryTestCase extends TestCase {

	/**
	 * test when an instance is assignable to an interface by default (no use of proxy)
	 */
	public void testClassIsImplementationOfInterface() throws NotCompliantMBeanException {
		Wilma wilma = new WilmaImpl();
		StandardMBean standardMBean = StandardMBeanFactory.buildStandardMBean(wilma, Wilma.class);

		assertEquals(Wilma.class, standardMBean.getMBeanInterface());
	}

	/**
	 * test when instance is NOT assignable to an interface but rather faked out via proxy
	 */
	public void testFakedImplementationViaProxy() throws NotCompliantMBeanException {
		FooBar fooBar = new FooBar();
		assertFalse(fooBar instanceof FooBarInterface);

		StandardMBean standardMBean = StandardMBeanFactory.buildStandardMBean(fooBar, FooBarInterface.class);

		assertEquals(FooBarInterface.class, standardMBean.getMBeanInterface());
	}

	/**
	 * test failure when attempting to use a class where an interface is required
	 */
	public void testFailureWhenManagementClassIsNotAnInterface() {
		FooBar fooBar = new FooBar();
		try {
			StandardMBeanFactory.buildStandardMBean(fooBar, ObjectName.class);
			fail("NotCompliantMBeanException should have been thrown");
		} catch (NotCompliantMBeanException ignore) {
		}
	}
}
