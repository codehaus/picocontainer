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

import org.picocontainer.Startable;
import org.picocontainer.Disposable;
import org.picocontainer.PicoContainer;
import org.picocontainer.ComponentAdapter;
import org.microcontainer.Kernel;
import org.microcontainer.McaDeployer;
import org.microcontainer.DeploymentException;
import org.microcontainer.DeploymentScriptHandler;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author Paul Hammant
 * @author Mike Ward
 * @version $Revision$
 */
public class DefaultKernel implements Kernel, Startable, Disposable {

	protected HashMap contextMap = null;
	protected McaDeployer mcaDeployer = null;
	protected DeploymentScriptHandler deploymentScriptHandler = null;

	public DefaultKernel(McaDeployer mcaDeployer, DeploymentScriptHandler deploymentScriptHandler) {
		contextMap = new HashMap();
		this.mcaDeployer = mcaDeployer;
		this.deploymentScriptHandler = deploymentScriptHandler;
	}

	protected void buildContainerAndRegisterContext(String context, boolean autoStart) throws DeploymentException {
		PicoContainer container = deploymentScriptHandler.handle(context, autoStart);
		contextMap.put(context, container);
	}

	protected void doDeploy(String context, URL mcaFile, boolean autoStart) throws DeploymentException {
		try {
			// deploy to work folder
			mcaDeployer.deploy(context, mcaFile);
			buildContainerAndRegisterContext(context, autoStart);

			// start container now, or defer until later
			/*if(start) {
				container.start();
			}*/
		} catch (IOException e) {
			throw new DeploymentException(e);
		}
	}

	public void deploy(String context, URL mcaFile) throws DeploymentException {
		doDeploy(context, mcaFile, true);
	}

	private void handleDeployForMcaFile(File mcaFile, boolean start) throws DeploymentException {
		try {
			String context = mcaFile.getName().split(".mca")[0];
			URL url = new URL("jar:file:" + mcaFile.getCanonicalPath() + "!/");
			this.doDeploy(context, url, start);
		} catch (IOException e) {
			throw new DeploymentException(e);
		}
	}

	public void deploy(File mcaFile) throws DeploymentException {
		handleDeployForMcaFile(mcaFile, true);
	}

	public void deploy(URL remoteMcaFile) throws DeploymentException {
		String[] file = remoteMcaFile.getFile().split("/|\\\\");
		String context = file[file.length - 1].split("\\.mca")[0];

		doDeploy(context, remoteMcaFile, true);
	}

	public void deferredDeploy(File mcaFile) throws DeploymentException {
		handleDeployForMcaFile(mcaFile, false);
	}

	public Object getComponent(String relativeComponentPath) {
        String[] path = relativeComponentPath.split("/");
		String context = path[0];
		String componentKey = path[1];

		PicoContainer pico = getRootContainer(context);
		Object result = pico.getComponentInstance(componentKey);

		if(result == null) {
			// find the correct component adapter for the class name passed in
			// this needed to be added because the search capability from pico
			// had been removed
			Collection adapters = pico.getComponentAdapters();

			for (Iterator iterator = adapters.iterator(); iterator.hasNext();) {
				ComponentAdapter componentAdapter = (ComponentAdapter) iterator.next();
				Class clazz = (Class)componentAdapter.getComponentKey(); // assume key is a Class (interface)

				if(clazz.getName().equals(componentKey)) {
					return componentAdapter.getComponentInstance(pico);
				}
			}
		}

		return result;
	}

    public PicoContainer getRootContainer(String context) {
        return (PicoContainer)contextMap.get(context);
    }

	public int size() {
		return contextMap.size();
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


