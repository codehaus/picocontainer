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

import org.hibernate.SessionFactory;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.nanocontainer.persistence.hibernate.SessionFactoryLifecycle;

/**
 * Test that lifecycle closes session factory
 * 
 * @version $Revision: 2043 $
 */
public class SessionFactoryLifecycleTestCase extends MockObjectTestCase {

    public void testThatLifecycleCallsClose() throws Exception {
        Mock sessionFactoryMock = mock(SessionFactory.class);
        sessionFactoryMock.expects(once()).method("close").withNoArguments();
        SessionFactoryLifecycle sfl = new SessionFactoryLifecycle((SessionFactory) sessionFactoryMock.proxy());
        sfl.stop();
    }

}
