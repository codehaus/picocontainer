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
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.nanocontainer.DefaultNanoContainer;
import org.nanocontainer.NanoContainer;
import org.nanocontainer.integrationkit.ContainerBuilder;
import org.nanocontainer.integrationkit.ContainerComposer;
import org.nanocontainer.integrationkit.ContainerPopulator;
import org.nanocontainer.integrationkit.ContainerRecorder;
import org.nanocontainer.reflection.DefaultContainerRecorder;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.ConstantParameter;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.ObjectReference;
import org.picocontainer.defaults.SimpleReference;

/**
 * <p>
 * ScopedContainerComposer is a 
 * {@link org.nanocontainer.integrationkit.ContainerComposer ContainerComposer} 
 * which can build PicoContainers for different web context scopes: 
 * application, session and request. 
 * </p>
 * <p>
 * The configuration for each scope is contained in one of more 
 * NanoContainer scripts.
 * The {@link org.nanocontainer.integrationkit.ContainerBuilder ContainerBuilder} 
 * used to build the PicoContainer and the names of scoped script files are configurable 
 * via a ScopedContainerConfigurator.
 * </p>
 * <p>
 * <b>Note:</b> ScopedContainerComposer requires ContainerBuilders that also implement
 * {@link org.nanocontainer.integrationkit.ContainerPopulator ContainerPopulator},
 * as this is used by the 
 * {@link org.nanocontainer.integrationkit.ContainerRecorder ContainerRecorder} proxy.
 * </p>
 * 
 * @author Mauro Talevi
 * @author Konstantin Pribluda ( konstantin.pribluda[at]infodesire.com )
 * @version $Revision$
 */
public class ScopedContainerComposer implements ContainerComposer {

	private static final String COMMA = ",";
    
    // ContainerBuilder class name
	private String containerBuilderClassName;
    // scoped container recorders
	private ContainerRecorder applicationRecorder;
    private ContainerRecorder requestRecorder;
    private ContainerRecorder sessionRecorder;

    /**
     * Creates a default ScopedContainerComposer
     * @throws ClassNotFoundException
     */
    public ScopedContainerComposer() throws ClassNotFoundException {
    	    this(new DefaultPicoContainer());
    }
    
    /**
     * Creates a configurable ScopedContainerComposer 
	 * @param configuration the PicoContainer holding the configuration
     * @throws ClassNotFoundException
     */
	public ScopedContainerComposer(PicoContainer configuration) throws ClassNotFoundException {
	    ScopedContainerConfigurator config = getConfigurator(configuration);
	    containerBuilderClassName = config.getContainerBuilder();

        MutablePicoContainer applicationContainerPrototype = new DefaultPicoContainer();
        applicationRecorder = new DefaultContainerRecorder(applicationContainerPrototype);
        populateContainer(config.getApplicationConfig(), applicationRecorder, null);

        MutablePicoContainer sessionContainerPrototype = new DefaultPicoContainer(applicationContainerPrototype);
        sessionRecorder = new DefaultContainerRecorder(sessionContainerPrototype);
        populateContainer(config.getSessionConfig(), sessionRecorder, applicationContainerPrototype);

        MutablePicoContainer requestContainerPrototype = new DefaultPicoContainer(sessionContainerPrototype);
        requestRecorder = new DefaultContainerRecorder(requestContainerPrototype);
        populateContainer(config.getRequestConfig(), requestRecorder, sessionContainerPrototype);
	}    

    public void composeContainer(MutablePicoContainer container, Object scope) {
        if (scope instanceof ServletContext) {
            applicationRecorder.replay(container);
        } else if (scope instanceof HttpSession) {
            sessionRecorder.replay(container);
        } else if (scope instanceof HttpServletRequest) {
            requestRecorder.replay(container);
        }
    }

    private ScopedContainerConfigurator getConfigurator(PicoContainer pico){
        ScopedContainerConfigurator configurator = (ScopedContainerConfigurator)pico.getComponentInstanceOfType(ScopedContainerConfigurator.class);
        if ( configurator == null ){
            configurator = new ScopedContainerConfigurator();
        }
        return configurator;
    }
    
	private void populateContainer(String resources, ContainerRecorder recorder, MutablePicoContainer parent) throws ClassNotFoundException {
	    MutablePicoContainer container = recorder.getContainerProxy();
	    String[] resourcePaths = toCSV(resources);
		for ( int i = 0; i < resourcePaths.length; i++ ){
			ContainerPopulator populator = createContainerPopulator(getResource(resourcePaths[i]), parent);
			populator.populateContainer(container);
		}
	}

	private String[] toCSV(String resources){
	    StringTokenizer st = new StringTokenizer(resources, COMMA);
	    List tokens = new ArrayList();
	    while ( st.hasMoreTokens() ){
	        tokens.add(st.nextToken().trim());	        
	    }
	    return (String[])tokens.toArray(new String[tokens.size()]);
	}
	
	private ContainerPopulator createContainerPopulator(Reader reader, MutablePicoContainer parent)
            throws ClassNotFoundException {
        NanoContainer nano = new DefaultNanoContainer(getClassLoader());
        Parameter[] parameters = new Parameter[] {
                new ConstantParameter(reader),
                new ConstantParameter(getClassLoader()) };
        nano.registerComponentImplementation(containerBuilderClassName,
                containerBuilderClassName, parameters);
        ContainerBuilder containerBuilder = (ContainerBuilder) nano
                .getPico().getComponentInstance(containerBuilderClassName);
        ObjectReference parentRef = new SimpleReference();
        parentRef.set(parent);
        containerBuilder.buildContainer(new SimpleReference(), parentRef, null, false);
        return (ContainerPopulator) containerBuilder;
    }

    private Reader getResource(String resource){
        return new InputStreamReader(getClassLoader().getResourceAsStream(resource));    	
    }

	private ClassLoader getClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}
    
}