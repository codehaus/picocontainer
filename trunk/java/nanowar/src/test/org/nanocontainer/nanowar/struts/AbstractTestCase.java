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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Stephen Molitor
 */
public abstract class AbstractTestCase extends MockObjectTestCase {
    protected HttpServletRequest request;
    protected HttpServletResponse response;
    protected ActionMapping mapping;
    protected MyDao dao;

    private Mock requestMock;
    private Mock responseMock;
    private MutablePicoContainer container;

    protected void setUp() {
        requestMock = mock(HttpServletRequest.class);
        request = (HttpServletRequest) requestMock.proxy();

        responseMock = mock(HttpServletResponse.class);
        response = (HttpServletResponse) responseMock.proxy();

        String actionType = MyAction.class.getName();
        mapping = new ActionMapping();
        mapping.setPath("/myPath1");
        mapping.setType(actionType);

        dao = new MyDao();
        container = new DefaultPicoContainer();
        container.registerComponentInstance(MyDao.class, dao);

        requestMock.stubs().method("getAttribute").with(eq(KeyConstants.ACTIONS_CONTAINER)).will(returnValue(container));
    }

}