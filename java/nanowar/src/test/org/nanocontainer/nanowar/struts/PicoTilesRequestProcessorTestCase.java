/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.nanowar.struts;

import org.apache.struts.action.ActionMapping;
import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.nanocontainer.nanowar.struts.PicoTilesRequestProcessor;
import org.nanocontainer.nanowar.struts.AbstractTestCase;
import org.nanocontainer.nanowar.struts.MyAction;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author Stephen Molitor
 */
public class PicoTilesRequestProcessorTestCase extends AbstractTestCase {

    public void testProcessActionCreate() throws IOException {
        PicoTilesRequestProcessor requestProcessor = new PicoTilesRequestProcessor();
        MyAction action = (MyAction) requestProcessor.processActionCreate(request, response, mapping);
        assertNotNull(action);
        assertSame(dao, action.getDao());
    }

}
