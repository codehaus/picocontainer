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

	protected HashMap contextMap = null;
	protected McaDeployer mcaDeployer = null;
	protected GroovyDeploymentScriptHandler groovyDeploymentScriptHandler = null;

	public DefaultKernel(McaDeployer mcaDeployer) {
		contextMap = new HashMap();
		this.mcaDeployer = mcaDeployer;
		groovyDeploymentScriptHandler = new GroovyDeploymentScriptHandler(mcaDeployer);
	}

	protected void doDeploy(String context, URL mcaFile, boolean autoStart) throws DeploymentException {
		try {
			// deploy to work folder
			mcaDeployer.deploy(context, mcaFile);

			PicoContainer container = groovyDeploymentScriptHandler.handle(context, autoStart);
			contextMap.put(context, container);

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
		return getRootContainer(path[0]).getComponentInstance(path[1]); // component key
	}

    public PicoContainer getRootContainer(String context) {
        return (PicoContainer)contextMap.get(context);
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


