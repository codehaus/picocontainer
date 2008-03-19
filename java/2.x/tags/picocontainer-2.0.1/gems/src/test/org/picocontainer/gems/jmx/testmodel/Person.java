/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Joerg Schaible                                           *
 *****************************************************************************/

package org.picocontainer.gems.jmx.testmodel;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;

import org.picocontainer.testmodel.PersonBean;


/**
 * @author J&ouml;rg Schaible
 */
public final class Person extends PersonBean implements PersonMBean {
    // PersonBean already fulfills PersonMBean interface

    private static final MBeanAttributeInfo[] attributes;
    static {
        attributes = new MBeanAttributeInfo[]{new MBeanAttributeInfo(
                "Name", String.class.getName(), "desc", true, true, false)};
    }

    /**
     * Default constructor.
     */
    public Person() {
        setName("John Doe");
    }

    /**
     * Construct a named person.
     * @param name The person's name.
     */
    public Person(final String name) {
        setName(name);
    }

    /**
     * @return Returns an explicit MBeanInfo for Person.
     */
    public static MBeanInfo createMBeanInfo() {
        return new MBeanInfo(Person.class.getName(), "Description of Person", attributes, null, null, null);
    }

}
