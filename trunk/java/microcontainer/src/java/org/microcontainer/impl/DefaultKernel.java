/*****************************************************************************
 * Copyright (C) MicroContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Paul Hammant                                             *
 *****************************************************************************/


package org.microcontainer.impl;

import org.microcontainer.*;
import org.picocontainer.Startable;
import org.picocontainer.Disposable;
import org.picocontainer.PicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.SimpleReference;
import org.picocontainer.defaults.ObjectReference;
import org.nanocontainer.script.groovy.GroovyContainerBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

/**
 * @author Paul Hammant
 * @author Mike Ward
 * @version $Revision$
 */
public class DefaultKernel implements Kernel, Startable, Disposable {
	protected ClassLoaderFactory classLoaderFactory = null;
	protected HashMap contextMap = null;
	protected MarDeployer marDeployer = null;

	public DefaultKernel() {
		classLoaderFactory = new ClassLoaderFactory();
		contextMap = new HashMap();
		marDeployer = new MarDeployer();
	}

	public void deploy(String context, URL marFile) throws DeploymentException {
		try {
			// deploy to work folder
			marDeployer.deploy(context, marFile);

			// build class loader
			ClassLoader classLoader = classLoaderFactory.build(context);

			// building container from script
			FileReader reader = new FileReader(marDeployer
					.getWorkingDir()
					.getCanonicalPath()
					.concat("/" + context + "/composition.groovy"));

			GroovyContainerBuilder gcb = new GroovyContainerBuilder(reader,	classLoader);
			MutablePicoContainer parent = new DefaultPicoContainer();
			parent.registerComponentInstance("hiddenClassLoader", classLoader);

			ObjectReference containerRef = new SimpleReference();
    		ObjectReference parentContainerRef = new SimpleReference();
			parentContainerRef.set(parent);
			gcb.buildContainer(containerRef, parentContainerRef, context);

			// map the child container to the context
        	contextMap.put(context, containerRef.get());

		} catch (IOException e) {
			throw new DeploymentException(e);
		}
	}

	public void deploy(File marFile) throws DeploymentException {
		try {
			String context = marFile.getName().split(".mar")[0];
			URL url = new URL("jar:file:" + marFile.getCanonicalPath() + "!/");
			this.deploy(context, url);
		} catch (IOException e) {
			throw new DeploymentException(e);
		}
	}

	public void deploy(URL remoteMarFile) throws DeploymentException {
		String[] file = remoteMarFile.getFile().split("/|\\\\");
		String context = file[file.length - 1].split("\\.mar")[0];

		deploy(context, remoteMarFile);
	}

	public void deferredDeploy(File file) {
	}

	public Object getComponent(String relativeComponentPath) {
		String[] path = relativeComponentPath.split("/");
		PicoContainer container = (PicoContainer)contextMap.get(path[0]); // context
		return container.getComponentInstance(path[1]); // class name
	}

	public void start(String startableNode) {
	}

	public void stop(String startableNode) {
		PicoContainer container = (PicoContainer)contextMap.get(startableNode);
		container.stop();
	}

	// Start all containers.
	public void start() {
	}

	// Stop all containers.
	public void stop() {
	}

	// Dispose of all containers.
	public void dispose() {
	}
}
