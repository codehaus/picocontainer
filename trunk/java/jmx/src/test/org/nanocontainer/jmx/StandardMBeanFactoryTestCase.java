package org.nanocontainer.jmx;

import junit.framework.TestCase;

import javax.management.ObjectName;
import javax.management.NotCompliantMBeanException;
import javax.management.StandardMBean;

import org.nanocontainer.jmx.testmodel.Person;
import org.nanocontainer.jmx.testmodel.PersonMBean;
import org.nanocontainer.testmodel.Wilma;
import org.nanocontainer.testmodel.WilmaImpl;
import org.picocontainer.testmodel.PersonBean;

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
		PersonBean person = new PersonBean();
		assertFalse(person instanceof PersonMBean);

		StandardMBean standardMBean = StandardMBeanFactory.buildStandardMBean(person, PersonMBean.class);

		assertEquals(PersonMBean.class, standardMBean.getMBeanInterface());
	}

	/**
	 * test failure when attempting to use a class where an interface is required
	 */
	public void testFailureWhenManagementClassIsNotAnInterface() {
		Person person = new Person();
		try {
			StandardMBeanFactory.buildStandardMBean(person, ObjectName.class);
			fail("NotCompliantMBeanException should have been thrown");
		} catch (NotCompliantMBeanException ignore) {
		}
	}
}
