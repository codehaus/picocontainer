/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.struts;

import org.apache.struts.action.ActionMapping;
import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author Stephen Molitor
 */
public class PicoRequestProcessorTestCase extends AbstractTestCase {

    public void testProcessActionCreate() throws IOException {
        PicoRequestProcessor requestProcessor = new PicoRequestProcessor();
        MyAction action = (MyAction) requestProcessor.processActionCreate(request, response, mapping);
        assertNotNull(action);
        assertSame(dao, action.getDao());
    }

}
