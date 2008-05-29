/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.web;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.ObjectReference;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.parameters.ConstantParameter;
import org.picocontainer.references.SimpleReference;
import org.picocontainer.script.ClassName;
import org.picocontainer.script.ContainerBuilder;
import org.picocontainer.script.ContainerComposer;
import org.picocontainer.script.ContainerPopulator;
import org.picocontainer.script.DefaultScriptedPicoContainer;
import org.picocontainer.script.ScriptedPicoContainer;

/**
 * <p>
 * ScopedContainerComposer is a 
 * {@link org.picocontainer.script.ContainerComposer ContainerComposer} 
 * which can build PicoContainers for different web context scopes: 
 * application, session and request. 
 * </p>
 * <p>
 * The configuration for each scope is contained in one of more scripts.
 * The {@link org.picocontainer.script.ContainerBuilder ContainerBuilder} 
 * used to build the PicoContainer and the names of scoped script files are configurable 
 * via a ScopedContainerConfigurator.
 * </p>
 * <p>
 * <b>Note:</b> ScopedContainerComposer requires ContainerBuilders that also implement
 * {@link org.picocontainer.script.ContainerPopulator ContainerPopulator},
 * as this is used by the 
 * {@link org.picocontainer.web.ContainerRecorder ContainerRecorder} proxy.
 * </p>
 * 
 * @author Mauro Talevi
 * @author Konstantin Pribluda ( konstantin.pribluda[at]infodesire.com )
 */
public final class ScopedContainerComposer implements ContainerComposer {

	private static final String COMMA = ",";
    
    // ContainerBuilder class name
	private final String containerBuilderClassName;
    // scoped container recorders
	private final ContainerRecorder applicationRecorder;
    private ContainerRecorder requestRecorder;
    private ContainerRecorder sessionRecorder;

    /**
     * Creates a default ScopedContainerComposer
     */
    public ScopedContainerComposer() {
    	    this(new DefaultPicoContainer());
    }
    
    /**
     * Creates a configurable ScopedContainerComposer 
	 * @param configuration the PicoContainer holding the configuration
     */
	public ScopedContainerComposer(PicoContainer configuration) {
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
        ScopedContainerConfigurator configurator = pico.getComponent(ScopedContainerConfigurator.class);
        if ( configurator == null ){
            configurator = new ScopedContainerConfigurator();
        }
        return configurator;
    }
    
	private void populateContainer(String resources, ContainerRecorder recorder, MutablePicoContainer parent) {
	    MutablePicoContainer container = recorder.getContainerProxy();
	    String[] resourcePaths = toCSV(resources);
        for (String resourcePath : resourcePaths) {
            ContainerPopulator populator = createContainerPopulator(getResource(resourcePath), parent);
            populator.populateContainer(container);
        }
    }

	private String[] toCSV(String resources){
	    StringTokenizer st = new StringTokenizer(resources, COMMA);
	    List<String> tokens = new ArrayList<String>();
	    while ( st.hasMoreTokens() ){
	        tokens.add(st.nextToken().trim());	        
	    }
	    return tokens.toArray(new String[tokens.size()]);
	}
	
	private ContainerPopulator createContainerPopulator(Reader reader, MutablePicoContainer parent) {
        ScriptedPicoContainer scripted = new DefaultScriptedPicoContainer(getClassLoader());
        Parameter[] parameters = new Parameter[] {
                new ConstantParameter(reader),
                new ConstantParameter(getClassLoader()) };
        scripted.addComponent(containerBuilderClassName,
                new ClassName(containerBuilderClassName), parameters);
        ContainerBuilder containerBuilder = (ContainerBuilder) scripted
                .getComponent(containerBuilderClassName);
        ObjectReference<PicoContainer> parentRef = new SimpleReference<PicoContainer>();
        parentRef.set(parent);
        containerBuilder.buildContainer(parent, null, false);
        return (ContainerPopulator) containerBuilder;
    }

    private Reader getResource(String resource){
        return new InputStreamReader(getClassLoader().getResourceAsStream(resource));    	
    }

	private ClassLoader getClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}
    
}