/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.nanowar.struts;

import java.io.IOException;

/**
 * @author Stephen Molitor
 */
public class PicoActionServletTestCase extends AbstractTestCase {

    public void testProcessActionCreate() throws IOException {
        PicoActionServlet servlet = new PicoActionServlet();
        MyAction action = (MyAction) servlet.processActionCreate(mapping, request);
        assertNotNull(action);
        assertSame(dao, action.getDao());
        assertSame(servlet, action.getServlet());
    }

}
