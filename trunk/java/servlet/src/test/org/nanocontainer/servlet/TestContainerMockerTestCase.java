/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.nanocontainer.servlet;

import junit.framework.TestCase;


/**
 * test case for TestContainerMock
 *
 * @author Konstantin Pribluda ( konstantin.pribluda[at]infodesire.com )
 * @version $Revision$
 */
public class TestContainerMockerTestCase extends TestCase implements KeyConstants {

    // TODO test what mock properly ?
    // Hmmm, a stop() is being called on DPC, when it already disposed.
    public void doNot_testThatItMocksProperly() throws Exception {

        TestContainerMocker mocker = new TestContainerMocker(XStreamContainerComposer.class);
        assertNull(mocker.getApplicationContainer());
        assertNull(mocker.getSessionContainer());
        assertNull(mocker.getRequestContainer());

        mocker.startApplication();
        assertNotNull(mocker.getApplicationContainer());
        mocker.startSession();
        assertNotNull(mocker.getSessionContainer());
        mocker.startRequest();
        assertNotNull(mocker.getRequestContainer());

        assertNotNull(mocker.getApplicationContainer().getComponentInstance("applicationScopedInstance"));
        assertNotNull(mocker.getSessionContainer().getComponentInstance("applicationScopedInstance"));
        assertNotNull(mocker.getRequestContainer().getComponentInstance("applicationScopedInstance"));

        assertNotNull(mocker.getRequestContainer().getComponentInstance("requestScopedInstance"));

        mocker.stopRequest();

        assertNull(mocker.getRequestContainer());

        mocker.startRequest();
        assertNotNull(mocker.getRequestContainer());

        assertSame(mocker.getApplicationContainer().getComponentInstance("applicationScopedInstance"),
                mocker.getRequestContainer().getComponentInstance("applicationScopedInstance"));

        mocker.stopApplication();

        assertNull(mocker.getApplicationContainer());
        assertNull(mocker.getSessionContainer());
        assertNull(mocker.getRequestContainer());
    }

    public void testFoo() {
        // boo
    }

}
