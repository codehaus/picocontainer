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

import javax.management.MBeanInfo;

import org.nanocontainer.jmx.testmodel.OtherPerson;
import org.nanocontainer.jmx.testmodel.Person;
import org.nanocontainer.jmx.testmodel.PersonMBean;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;


/**
 * @author J&ouml;rg Schaible
 */
public class ComponentKeyConventionMBeanInfoProviderTest extends MockObjectTestCase {

    private MutablePicoContainer pico;
    private MBeanInfoProvider mBeanProvider;

    protected void setUp() throws Exception {
        super.setUp();
        pico = new DefaultPicoContainer();
        mBeanProvider = new ComponentKeyConventionMBeanInfoProvider();
    }

    public void testMBeanInfoIsDeterminedIfKeyIsType() {
        final PersonMBean person = new OtherPerson();

        final Mock mockComponentAdapter = mock(ComponentAdapter.class);
        mockComponentAdapter.stubs().method("getComponentKey").will(returnValue(Person.class));
        mockComponentAdapter.stubs().method("getComponentImplementation").will(returnValue(person.getClass()));

        pico.registerComponent((ComponentAdapter)mockComponentAdapter.proxy());
        pico.registerComponentInstance(Person.class.getName() + "MBeanInfo", Person.createMBeanInfo());

        final MBeanInfo info = mBeanProvider.provide(pico, (ComponentAdapter)mockComponentAdapter.proxy());
        assertNotNull(info);
        assertEquals(Person.createMBeanInfo().getDescription(), info.getDescription());
    }

    public void testMBeanInfoIsDeterminedIfKeyIsManagementInterface() {
        final ComponentAdapter componentAdapter = pico.registerComponentImplementation(PersonMBean.class, Person.class);
        pico.registerComponentInstance(PersonMBean.class.getName() + "Info", Person.createMBeanInfo());

        final MBeanInfo info = mBeanProvider.provide(pico, componentAdapter);
        assertNotNull(info);
        assertEquals(Person.createMBeanInfo().getDescription(), info.getDescription());
    }

    public void testMBeanInfoIsDeterminedIfKeyIsString() {

        final ComponentAdapter componentAdapter = pico.registerComponentImplementation("JUnit", Person.class);
        pico.registerComponentInstance("JUnitMBeanInfo", Person.createMBeanInfo());

        final MBeanInfo info = mBeanProvider.provide(pico, componentAdapter);
        assertNotNull(info);
        assertEquals(Person.createMBeanInfo().getDescription(), info.getDescription());
    }

}
