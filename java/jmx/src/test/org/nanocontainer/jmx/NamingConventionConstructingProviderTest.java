/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Joerg Schaible                                           *
 *****************************************************************************/

package org.nanocontainer.jmx;

import javax.management.DynamicMBean;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.StandardMBean;

import org.nanocontainer.jmx.testmodel.OtherPerson;
import org.nanocontainer.jmx.testmodel.Person;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;


/**
 * @author J&ouml;rg Schaible
 */
public class NamingConventionConstructingProviderTest extends MockObjectTestCase {

    private Mock mockObjectNameFactory;
    private ObjectName objectName;
    private MutablePicoContainer pico;

    protected void setUp() throws Exception {
        super.setUp();
        mockObjectNameFactory = mock(ObjectNameFactory.class);
        objectName = new ObjectName(":type=JUnit");
        pico = new DefaultPicoContainer();
    }

    public void testObjectNameFactoryMustNotBeNull() {
        try {
            new NamingConventionConstructingProvider(null);
            fail("NullPointerException expected");
        } catch (final NullPointerException e) {
        }
    }

    public void testManagementInterfaceIsDeterminedFromMBeanInfo() {
        mockObjectNameFactory.expects(once()).method("create").with(same(OtherPerson.class), isA(DynamicMBean.class)).will(
                returnValue(objectName));

        final ComponentAdapter componentAdapter = pico.registerComponentImplementation(OtherPerson.class);
        pico.registerComponentInstance(OtherPerson.class.getName() + "MBeanInfo", Person.createMBeanInfo());
        final DynamicMBeanProvider provider = new NamingConventionConstructingProvider((ObjectNameFactory)mockObjectNameFactory
                .proxy());

        final JMXRegistrationInfo info = provider.provide(pico, componentAdapter);
        assertNotNull(info);
        assertTrue(((StandardMBean)info.getMBean()).getImplementation() instanceof OtherPerson);
    }

    public void testManagementInterfaceIsDeterminedWithoutMBeanInfo() {
        mockObjectNameFactory.expects(once()).method("create").with(same(Person.class), isA(DynamicMBean.class)).will(
                returnValue(objectName));

        final ComponentAdapter componentAdapter = pico.registerComponentImplementation(Person.class);
        final DynamicMBeanProvider provider = new NamingConventionConstructingProvider((ObjectNameFactory)mockObjectNameFactory
                .proxy());

        final JMXRegistrationInfo info = provider.provide(pico, componentAdapter);
        assertNotNull(info);
        assertTrue(((StandardMBean)info.getMBean()).getImplementation() instanceof Person);
    }

    public void testNoInstanceIsCreatedIfManagementInterfaceIsMissing() {
        final Mock mockComponentAdapter = mock(ComponentAdapter.class);
        mockComponentAdapter.stubs().method("getComponentKey").will(returnValue(OtherPerson.class));
        mockComponentAdapter.stubs().method("getComponentImplementation").will(returnValue(OtherPerson.class));

        final ComponentAdapter componentAdapter = pico.registerComponentImplementation(OtherPerson.class);
        final DynamicMBeanProvider provider = new NamingConventionConstructingProvider((ObjectNameFactory)mockObjectNameFactory
                .proxy());

        assertNull(provider.provide(pico, componentAdapter));
    }

    public void testObjectNameMustBeGiven() {
        mockObjectNameFactory.expects(once()).method("create").with(same(Person.class), isA(DynamicMBean.class)).will(
                returnValue(null));

        final ComponentAdapter componentAdapter = pico.registerComponentImplementation(Person.class);
        final DynamicMBeanProvider provider = new NamingConventionConstructingProvider((ObjectNameFactory)mockObjectNameFactory
                .proxy());

        assertNull(provider.provide(pico, componentAdapter));
    }

    public void testMalformedObjectNameThrowsJMXRegistrationException() {
        mockObjectNameFactory.expects(once()).method("create").with(same(Person.class), isA(DynamicMBean.class)).will(
                throwException(new MalformedObjectNameException("JUnit")));

        final ComponentAdapter componentAdapter = pico.registerComponentImplementation(Person.class);
        final DynamicMBeanProvider provider = new NamingConventionConstructingProvider((ObjectNameFactory)mockObjectNameFactory
                .proxy());

        try {
            provider.provide(pico, componentAdapter);
            fail("JMXRegistrationException expected");
        } catch (final JMXRegistrationException e) {
            assertEquals("JUnit", e.getCause().getMessage());
        }
    }
}
