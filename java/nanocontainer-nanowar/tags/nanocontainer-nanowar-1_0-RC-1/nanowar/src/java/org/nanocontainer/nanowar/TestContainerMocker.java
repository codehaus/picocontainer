/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.nanocontainer.nanowar;

import org.jmock.Mock;
import org.nanocontainer.integrationkit.ContainerBuilder;
import org.nanocontainer.integrationkit.ContainerComposer;
import org.nanocontainer.integrationkit.DefaultLifecycleContainerBuilder;
import org.nanocontainer.integrationkit.PicoCompositionException;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.SimpleReference;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * create test container mocker for specified container composer class
 * supplied class shall be  instance of ContainerComposer, or IAE will be thrown
 *
 * @author Konstantin Pribluda
 * @version $Revision$
 */
public class TestContainerMocker implements KeyConstants {

    private final ContainerBuilder containerKiller = new DefaultLifecycleContainerBuilder(null);
    /**
     * application level container
     */
    MutablePicoContainer application;
    /**
     * sesison level container
     */
    MutablePicoContainer session;
    /**
     * request level container
     */
    MutablePicoContainer request;

    ContainerBuilder containerBuilder;

    public TestContainerMocker(Class containerComposerClass) {
        try {
            containerBuilder = new DefaultLifecycleContainerBuilder((ContainerComposer) containerComposerClass.newInstance());
        } catch (Exception ex) {
            throw new PicoCompositionException(ex);
        }
    }

    /**
     * fake application start
     */
    public void startApplication() {
        SimpleReference ref = new SimpleReference();
        containerBuilder.buildContainer(ref, new SimpleReference(), (new Mock(ServletContext.class)).proxy(), false);
        application = (MutablePicoContainer) ref.get();
    }

    /**
     * fake application stop
     */
    public void stopApplication() {
        SimpleReference ref = new SimpleReference();
        ref.set(application);
        containerKiller.killContainer(ref);

        // and reset all the containers
        application = null;
        session = null;
        request = null;
    }

    /**
     * fake new session
     */
    public void startSession() {
        SimpleReference ref = new SimpleReference();
        SimpleReference parent = new SimpleReference();
        parent.set(application);
        containerBuilder.buildContainer(ref, parent, ((new Mock(HttpSession.class)).proxy()), false);
        session = (MutablePicoContainer) ref.get();

    }

    /**
     * fake session invalidation
     */
    public void stopSession() {
        SimpleReference ref = new SimpleReference();
        ref.set(session);
        containerKiller.killContainer(ref);

        session = null;
        request = null;
    }


    /**
     * fake request start
     */
    public void startRequest() {

        SimpleReference ref = new SimpleReference();
        SimpleReference parent = new SimpleReference();
        parent.set(session);
        containerBuilder.buildContainer(ref, parent, (new Mock(HttpServletRequest.class)).proxy(), false);
        request = (MutablePicoContainer) ref.get();

    }

    /**
     * fake request stop
     */
    public void stopRequest() {

        SimpleReference ref = new SimpleReference();
        ref.set(request);
        containerKiller.killContainer(ref);
        request = null;
    }


    public PicoContainer getApplicationContainer() {
        return application;
    }


    public PicoContainer getSessionContainer() {
        return session;
    }

    public PicoContainer getRequestContainer() {
        return request;
    }
}
