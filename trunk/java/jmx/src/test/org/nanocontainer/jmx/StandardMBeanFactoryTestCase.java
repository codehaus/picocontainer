package org.nanocontainer.jmx;

import javax.management.DynamicMBean;

import org.nanocontainer.jmx.testmodel.Person;
import org.nanocontainer.jmx.testmodel.PersonMBean;

import junit.framework.TestCase;

/**
 * @author Michael Ward
 * @author J&ouml;rg Schaible
 * @version $Revision$
 */
public class StandardMBeanFactoryTestCase extends TestCase {

    public void testMBeanCreationWithMBeanInfo() {
        final DynamicMBeanFactory factory = new StandardMBeanFactory();
        try {
            factory.create(new Person(), Person.createMBeanInfo());
            fail("JMXRegistrationException expected");
        } catch(final JMXRegistrationException e) {
        }
    }

    public void testMBeanCeationWithManagementInterfaceOnly() {
        final DynamicMBeanFactory factory = new StandardMBeanFactory();
        final DynamicMBean mBean = factory.create(new Person(), PersonMBean.class);
        assertNotNull(mBean);
    }
}
