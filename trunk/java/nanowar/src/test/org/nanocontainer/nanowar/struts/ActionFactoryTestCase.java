/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.nanowar.struts;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionServlet;
import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.defaults.DefaultPicoContainer;

/**
 * @author Stephen Molitor
 */
public class ActionFactoryTestCase extends MockObjectTestCase {

    private Mock requestMock = mock(HttpServletRequest.class);
    private HttpServletRequest request = (HttpServletRequest) requestMock.proxy();

    private Mock sessionMock = mock(HttpSession.class);
    private HttpSession session = (HttpSession) sessionMock.proxy();

    private Mock servletContextMock = mock(ServletContext.class);
    private ServletContext servletContext = (ServletContext) servletContextMock.proxy();

    private Mock servletMock = mock(ActionServlet.class);
    private ActionServlet servlet = (ActionServlet) servletMock.proxy();

    private ActionMapping mapping1;
    private ActionMapping mapping2;

    private ActionFactory actionFactory;
    private MyDao dao;

    public void testActionContainerCreatedOnlyOncePerRequest() {
        MutablePicoContainer requestContainer = new DefaultPicoContainer();
        requestContainer.registerComponentImplementation(MyDao.class);
        MutablePicoContainer actionsContainer = new DefaultPicoContainer(requestContainer);

        requestMock.expects(once()).method("getAttribute").with(eq(KeyConstants.ACTIONS_CONTAINER)).will(
                returnValue(actionsContainer));
        requestMock.expects(once()).method("getAttribute").with(eq(KeyConstants.ACTIONS_CONTAINER)).will(
                returnValue(actionsContainer));
        requestMock.expects(once()).method("setAttribute").with(eq(KeyConstants.ACTIONS_CONTAINER),
                isA(MutablePicoContainer.class));
        requestMock.expects(once()).method("getAttribute").with(eq(KeyConstants.REQUEST_CONTAINER)).will(
                returnValue(requestContainer));
        requestMock.expects(once()).method("getAttribute").with(eq(KeyConstants.ACTIONS_CONTAINER)).will(
                returnValue(null));

        actionFactory.getAction(request, mapping1, servlet);
        actionFactory.getAction(request, mapping1, servlet);
        actionFactory.getAction(request, mapping1, servlet);
    }

    public void testGetActionWhenActionsContainerAlreadyExists() {
        MutablePicoContainer requestContainer = new DefaultPicoContainer();
        requestContainer.registerComponentInstance(MyDao.class, dao);
        MutablePicoContainer actionsContainer = new DefaultPicoContainer(requestContainer);

        requestMock.stubs().method("getAttribute").with(eq(KeyConstants.ACTIONS_CONTAINER)).will(
                returnValue(actionsContainer));

        MyAction action1 = (MyAction) actionFactory.getAction(request, mapping1, servlet);
        MyAction action2 = (MyAction) actionFactory.getAction(request, mapping2, servlet);
        MyAction action3 = (MyAction) actionFactory.getAction(request, mapping1, servlet);
        MyAction action4 = (MyAction) actionFactory.getAction(request, mapping2, servlet);

        assertNotNull(action1);
        assertNotNull(action2);
        assertNotSame(action1, action2);
        assertSame(action1, action3);
        assertSame(action2, action4);

        assertSame(action1, actionsContainer.getComponentInstance("/myPath1"));
        assertSame(action2, actionsContainer.getComponentInstance("/myPath2"));

        assertSame(dao, action1.getDao());
        assertSame(dao, action2.getDao());

        assertNotNull(action1.getServlet());
        assertNotNull(action2.getServlet());
        assertSame(servlet, action1.getServlet());
        assertSame(servlet, action2.getServlet());
    }

    public void testRequestContainerExists() {
        MutablePicoContainer requestContainer = new DefaultPicoContainer();
        requestContainer.registerComponentInstance(MyDao.class, dao);

        requestMock.expects(once()).method("getAttribute").with(eq(KeyConstants.ACTIONS_CONTAINER)).will(
                returnValue(null));
        requestMock.expects(once()).method("getAttribute").with(eq(KeyConstants.REQUEST_CONTAINER)).will(
                returnValue(requestContainer));
        requestMock.expects(once()).method("setAttribute").with(eq(KeyConstants.ACTIONS_CONTAINER),
                isA(MutablePicoContainer.class));

        MyAction action = (MyAction) actionFactory.getAction(request, mapping1, servlet);
        assertNotNull(action);
        assertSame(dao, action.getDao());
    }

    public void testSessionContainerExists() {
        MutablePicoContainer sessionContainer = new DefaultPicoContainer();
        sessionContainer.registerComponentInstance(MyDao.class, dao);

        requestMock.expects(once()).method("getAttribute").with(eq(KeyConstants.ACTIONS_CONTAINER)).will(
                returnValue(null));
        requestMock.expects(once()).method("getAttribute").with(eq(KeyConstants.REQUEST_CONTAINER)).will(
                returnValue(null));
        sessionMock.expects(once()).method("getAttribute").with(eq(KeyConstants.SESSION_CONTAINER)).will(
                returnValue(sessionContainer));
        requestMock.expects(once()).method("setAttribute").with(eq(KeyConstants.ACTIONS_CONTAINER),
                isA(MutablePicoContainer.class));

        MyAction action = (MyAction) actionFactory.getAction(request, mapping1, servlet);
        assertNotNull(action);
        assertSame(dao, action.getDao());
    }

    public void testApplicationContainerExists() {
        MutablePicoContainer appContainer = new DefaultPicoContainer();
        appContainer.registerComponentInstance(MyDao.class, dao);

        requestMock.expects(once()).method("getAttribute").with(eq(KeyConstants.ACTIONS_CONTAINER)).will(
                returnValue(null));
        requestMock.expects(once()).method("getAttribute").with(eq(KeyConstants.REQUEST_CONTAINER)).will(
                returnValue(null));
        sessionMock.expects(once()).method("getAttribute").with(eq(KeyConstants.SESSION_CONTAINER)).will(
                returnValue(null));
        servletContextMock.expects(once()).method("getAttribute").with(eq(KeyConstants.APPLICATION_CONTAINER)).will(
                returnValue(appContainer));
        requestMock.expects(once()).method("setAttribute").with(eq(KeyConstants.ACTIONS_CONTAINER),
                isA(MutablePicoContainer.class));

        MyAction action = (MyAction) actionFactory.getAction(request, mapping1, servlet);
        assertNotNull(action);
        assertSame(dao, action.getDao());
    }

    public void testNoContainerExists() {
        requestMock.expects(once()).method("getAttribute").with(eq(KeyConstants.ACTIONS_CONTAINER)).will(
                returnValue(null));
        requestMock.expects(once()).method("getAttribute").with(eq(KeyConstants.REQUEST_CONTAINER)).will(
                returnValue(null));
        sessionMock.expects(once()).method("getAttribute").with(eq(KeyConstants.SESSION_CONTAINER)).will(
                returnValue(null));
        servletContextMock.expects(once()).method("getAttribute").with(eq(KeyConstants.APPLICATION_CONTAINER)).will(
                returnValue(null));

        try {
            actionFactory.getAction(request, mapping1, servlet);
            fail("PicoInitializationException should have been raised");
        } catch (PicoInitializationException e) {
            // expected
        }
    }

    public void testBadActionType() {
        MutablePicoContainer actionsContainer = new DefaultPicoContainer();
        requestMock.stubs().method("getAttribute").with(eq(KeyConstants.ACTIONS_CONTAINER)).will(
                returnValue(actionsContainer));

        mapping1.setType("/i/made/a/typo");
        try {
            actionFactory.getAction(request, mapping1, servlet);
            fail("PicoIntrospectionException should have been raised");
        } catch (PicoIntrospectionException e) {
            // expected
        }
    }

    protected void setUp() {
        String actionType = MyAction.class.getName();

        mapping1 = new ActionMapping();
        mapping1.setPath("/myPath1");
        mapping1.setType(actionType);

        mapping2 = new ActionMapping();
        mapping2.setPath("/myPath2");
        mapping2.setType(actionType);

        requestMock.stubs().method("getSession").will(returnValue(session));
        sessionMock.stubs().method("getServletContext").will(returnValue(servletContext));

        actionFactory = new ActionFactory();
        dao = new MyDao();
    }

}