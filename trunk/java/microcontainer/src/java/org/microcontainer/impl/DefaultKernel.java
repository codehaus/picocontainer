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

import java.io.File;
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
	protected GroovyDeploymentScriptHandler groovyDeploymentScriptHandler = null;

	public DefaultKernel() {
		classLoaderFactory = new ClassLoaderFactory();
		contextMap = new HashMap();
		marDeployer = new MarDeployer();
		groovyDeploymentScriptHandler = new GroovyDeploymentScriptHandler();
	}

	protected void doDeploy(String context, URL marFile, boolean start) throws DeploymentException {
		try {
			// deploy to work folder
			marDeployer.deploy(context, marFile);

			PicoContainer container = groovyDeploymentScriptHandler.handle(context, marFile);
			contextMap.put(context, container);

			// start container now, or defer until later
			if(start) {
				container.start();
			}
		} catch (IOException e) {
			throw new DeploymentException(e);
		}
	}

	public void deploy(String context, URL marFile) throws DeploymentException {
		doDeploy(context, marFile, true);
	}

	private void handleDeployForMarFile(File marFile, boolean start) throws DeploymentException {
		try {
			String context = marFile.getName().split(".mar")[0];
			URL url = new URL("jar:file:" + marFile.getCanonicalPath() + "!/");
			this.doDeploy(context, url, start);
		} catch (IOException e) {
			throw new DeploymentException(e);
		}
	}

	public void deploy(File marFile) throws DeploymentException {
		handleDeployForMarFile(marFile, true);
	}

	public void deploy(URL remoteMarFile) throws DeploymentException {
		String[] file = remoteMarFile.getFile().split("/|\\\\");
		String context = file[file.length - 1].split("\\.mar")[0];

		doDeploy(context, remoteMarFile, true);
	}

	public void deferredDeploy(File marFile) throws DeploymentException {
		handleDeployForMarFile(marFile, false);
	}

	public Object getComponent(String relativeComponentPath) {
		String[] path = relativeComponentPath.split("/");
		PicoContainer container = (PicoContainer)contextMap.get(path[0]); // context
		return container.getComponentInstance(path[1]); // class name
	}

	public void start(String startableNode) {
		PicoContainer container = (PicoContainer)contextMap.get(startableNode);
		container.start();
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


