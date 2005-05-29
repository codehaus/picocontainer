/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by various                           *
 *****************************************************************************/

package org.nanocontainer.persistence.hibernate;

import org.nanocontainer.persistence.hibernate.ConstructableConfiguration;

import junit.framework.TestCase;

/**
 * @version $Revision$
 */
public class ConstructableConfigurationTestCase extends TestCase {

    public void testDefaultConstruction() throws Exception {
        ConstructableConfiguration config = new ConstructableConfiguration();
        assertNotNull(config);
    }

    public void testResourceConstruction() throws Exception {
        ConstructableConfiguration config = new ConstructableConfiguration("/hibernate3.cfg.xml");
        assertNotNull(config);
    }

}
