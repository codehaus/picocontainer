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
 * JMXVisitor that defaults to a uses MX4JDynamicMBeanFactory. The implementation will automatically instantiate and use a
 * {@link MX4JDynamicMBeanFactory}if no {@link DynamicMBeanFactory}was registered in the visited {@link PicoContainer}.
 * @author J&ouml;rg Schaible
 * @since 1.0
 */
public class MX4JVisitor extends JMXVisitor {

    /**
     * Create and return a {@link MX4JDynamicMBeanFactory}.
     * @see org.nanocontainer.jmx.JMXVisitor#createDynamicMBeanFactory()
     */
    protected DynamicMBeanFactory createDynamicMBeanFactory() {
        return new MX4JDynamicMBeanFactory();
    }

}
