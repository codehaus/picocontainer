/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Joerg Schaible                                           *
 *****************************************************************************/

package org.nanocontainer.jmx.testmodel;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;

import org.picocontainer.testmodel.PersonBean;

/**
 * @author J&ouml;rg Schaible
 */
public class Person extends PersonBean implements PersonMBean {
    // PersonBean already fulfills PersonMBean interface
    private static MBeanAttributeInfo[] attributes;

    static {
        attributes = new MBeanAttributeInfo[] {
            new MBeanAttributeInfo("Name", String.class.getName(), "desc", true, true, false)
        };
    }

    public Person() {
        setName("John Doe");
    }

    public static MBeanInfo createMBeanInfo() {
        return new MBeanInfo(Person.class.getName(), "description", attributes, null, null, null);
    }

}
