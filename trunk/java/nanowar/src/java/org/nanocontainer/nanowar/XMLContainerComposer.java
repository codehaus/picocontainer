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

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.nanocontainer.SoftCompositionPicoContainer;
import org.nanocontainer.integrationkit.ContainerComposer;
import org.nanocontainer.integrationkit.ContainerPopulator;
import org.nanocontainer.integrationkit.ContainerRecorder;
import org.nanocontainer.reflection.DefaultContainerRecorder;
import org.nanocontainer.reflection.DefaultSoftCompositionPicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.defaults.ConstantParameter;

/**
 * Configurable ContainerComposer reading from XML files.
 * The ContainerBuilder and the scoped XML files are configurable
 * via a Map, using the appropriate key.  If any configuration 
 * element is missing, the default is applied.
 * 
 * @author Mauro Talevi
 * @author Konstantin Pribluda ( konstantin.pribluda[at]infodesire.com )
 * @version $Revision$
 */
public class XMLContainerComposer implements ContainerComposer {

	public final static String APPLICATION_CONFIG_KEY = "applicationConfig";   
	public final static String SESSION_CONFIG_KEY = "sessionConfig";   
	public final static String REQUEST_CONFIG_KEY = "requestConfig";   
	public final static String CONTAINER_BUILDER_KEY = "containerBuilder";   

    public final static String[] DEFAULT_APPLICATION_CONFIG = new String[]{"nano-application.xml"};
    public final static String[] DEFAULT_SESSION_CONFIG = new String[]{"nano-session.xml"};
    public final static String[] DEFAULT_REQUEST_CONFIG = new String[]{"nano-request.xml"};
	public final static String DEFAULT_CONTAINER_BUILDER = "org.nanocontainer.script.xml.XStreamContainerBuilder";   

    // scoped container recorders
	private ContainerRecorder applicationRecorder;
    private ContainerRecorder requestRecorder;
    private ContainerRecorder sessionRecorder;
	// configurable ContainerBuilder class name
	private String containerBuilderClassName;
	
    /**
     * Creates a default XMLContainerComposer
     * @throws ClassNotFoundException
     */
    public XMLContainerComposer() throws ClassNotFoundException {
    	this(new HashMap());
    }
    
    /**
     * Creates a configurable XMLContainerComposer 
	 * @param config the Map containing the configuration 
     * @throws ClassNotFoundException
     */
	public XMLContainerComposer(Map config) throws ClassNotFoundException {

	    containerBuilderClassName = (String)config.get(CONTAINER_BUILDER_KEY);
	    if ( containerBuilderClassName == null ){
	        containerBuilderClassName = DEFAULT_CONTAINER_BUILDER;
	    }
	    
        applicationRecorder = new DefaultContainerRecorder(new DefaultSoftCompositionPicoContainer());
        String[] applicationConfig = (String[])config.get(APPLICATION_CONFIG_KEY);
        if ( applicationConfig == null ){
            applicationConfig = DEFAULT_APPLICATION_CONFIG;
        }        
        populateContainer(applicationConfig, applicationRecorder);

        sessionRecorder = new DefaultContainerRecorder(new DefaultSoftCompositionPicoContainer());
        String[] sessionConfig = (String[])config.get(SESSION_CONFIG_KEY);
        if ( sessionConfig == null ){
            sessionConfig = DEFAULT_SESSION_CONFIG;
        }
        populateContainer(sessionConfig, sessionRecorder);
        
        requestRecorder = new DefaultContainerRecorder(new DefaultSoftCompositionPicoContainer());
        String[] requestConfig = (String[])config.get(REQUEST_CONFIG_KEY);
        if ( requestConfig == null ){
            requestConfig = DEFAULT_REQUEST_CONFIG;
        }
        populateContainer(requestConfig, requestRecorder);
	}    
    	
	private void populateContainer(String[] resources, ContainerRecorder recorder) throws ClassNotFoundException {
	    SoftCompositionPicoContainer container = recorder.getContainerProxy();
		for ( int i = 0; i < resources.length; i++ ){
			ContainerPopulator builder = createContainerPopulator(getResource(resources[i]));
			builder.populateContainer(container);
		}
	}

	private ContainerPopulator createContainerPopulator(Reader reader) throws ClassNotFoundException {
        SoftCompositionPicoContainer softPico = new DefaultSoftCompositionPicoContainer(getClassLoader());
		Parameter[] parameters = new Parameter[]{new ConstantParameter(reader), new ConstantParameter(getClassLoader())};
		softPico.registerComponentImplementation(containerBuilderClassName, containerBuilderClassName,
												 parameters);
        return (ContainerPopulator)softPico.getComponentInstance(containerBuilderClassName);
	}


    private Reader getResource(String resource){
    	return new InputStreamReader(getClassLoader().getResourceAsStream(resource));    	
    }

	private ClassLoader getClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}

	/**
	 * {@inheritDoc}
	 * @see ContainerComposer#composeContainer(SoftCompositionPicoContainer, Object)
	 */
    public void composeContainer(SoftCompositionPicoContainer container, Object scope) {
        if (scope instanceof ServletContext) {
            applicationRecorder.replay(container);
        } else if (scope instanceof HttpSession) {
            sessionRecorder.replay(container);
        } else if (scope instanceof HttpServletRequest) {
            requestRecorder.replay(container);
        }
    }
    
}
