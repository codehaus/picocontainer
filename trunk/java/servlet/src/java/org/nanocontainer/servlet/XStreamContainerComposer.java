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

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;

import org.nanocontainer.script.xml.XStreamContainerBuilder;
import org.nanocontainer.reflection.recorder.ContainerRecorder;

import org.nanocontainer.integrationkit.ContainerComposer;
import org.nanocontainer.integrationkit.PicoCompositionException;

import java.io.InputStreamReader;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * container composer reading from xml files via xstream. it pulls configuration from
 * xml files placed on classpath of web application ( WEB-INF/classes/ ) , and their names are:
 * nano-application.xml , nano-session.xml and nano-request.xml 
 *
 * @author Konstantin Pribluda ( konstantin[at]infodesire.com ) 
 * @version $Revision$
 */
public class XStreamContainerComposer implements ContainerComposer {
	
	// for now, we just use hardvired configuration files. 
    public final static String APPLICATION_CONFIG = "nano-application.xml";
    public final static String SESSION_CONFIG = "nano-session.xml";
    public final static String REQUEST_CONFIG = "nano-request.xml";
	
	
	// request and session level container recorders. 
	// we do not need one for application scope - this happens really seldom
    private ContainerRecorder requestRecorder;
    private ContainerRecorder sessionRecorder;
	
    /**
     * Constructor for the ContainerAssembler object
     */
    public XStreamContainerComposer() {
		
        requestRecorder = new ContainerRecorder(new DefaultPicoContainer());
        sessionRecorder = new ContainerRecorder(new DefaultPicoContainer());
		
        // create and populate request scope
        XStreamContainerBuilder reqBuilder = new XStreamContainerBuilder(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(REQUEST_CONFIG)), Thread.currentThread().getContextClassLoader());
        reqBuilder.populateContainer(requestRecorder.getContainerProxy());

        // create and populate session scope
        XStreamContainerBuilder sessionBuilder = new XStreamContainerBuilder(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(SESSION_CONFIG)), Thread.currentThread().getContextClassLoader());
        sessionBuilder.populateContainer(sessionRecorder.getContainerProxy());
		
	}
	
 	/**
     * compose desired container 
     *
     * @param container  Description of Parameter
     * @param scope      Description of Parameter
     */
    public void composeContainer(MutablePicoContainer container, Object scope) {
		
        if (scope instanceof ServletContext) {

            XStreamContainerBuilder appBuilder = new XStreamContainerBuilder(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(APPLICATION_CONFIG)), Thread.currentThread().getContextClassLoader());
            appBuilder.populateContainer(container);
        }
        else if (scope instanceof HttpSession) {
			try {
                sessionRecorder.replay(container);
            } catch (Exception ex) {
                throw new PicoCompositionException("session container composition failed", ex);
            }
        }
        else if (scope instanceof HttpServletRequest) {
            try {
                requestRecorder.replay(container);
            } catch (Exception ex) {
                throw new PicoCompositionException("request container composition failed", ex);
            }
        }
    }
}
