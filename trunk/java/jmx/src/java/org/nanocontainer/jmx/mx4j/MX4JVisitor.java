/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Joerg Schaible                                           *
 *****************************************************************************/

package org.nanocontainer.jmx.mx4j;

import org.nanocontainer.jmx.DynamicMBeanFactory;
import org.nanocontainer.jmx.JMXVisitor;
import org.picocontainer.PicoContainer;


/**
 * JMXVisitor that automatically
 * @author J&ouml;rg Schaible
 * @since 1.0
 */
public class MX4JVisitor extends JMXVisitor {
    private DynamicMBeanFactory dynamicMBeanFactory;

    protected DynamicMBeanFactory getDynamicMBeanFactory(final PicoContainer picoContainer) {
        final DynamicMBeanFactory factory = (DynamicMBeanFactory)picoContainer.getComponentInstance(DynamicMBeanFactory.class);
        if (factory == null) {
            if (dynamicMBeanFactory == null) {
                dynamicMBeanFactory = new MX4JDynamicMBeanFactory();
            }
            return dynamicMBeanFactory;
        } else {
            return factory;
        }
    }

}
