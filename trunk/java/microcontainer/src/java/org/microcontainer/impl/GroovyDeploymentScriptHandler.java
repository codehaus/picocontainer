package org.microcontainer.impl;

import org.picocontainer.PicoContainer;
import org.microcontainer.DeploymentScript;
import org.microcontainer.DeploymentException;
import org.codehaus.groovy.control.CompilationFailedException;

import java.net.URL;
import java.io.File;
import java.io.IOException;

import groovy.lang.GroovyClassLoader;

/**
 * 
 */
public class GroovyDeploymentScriptHandler {
	protected ClassLoaderFactory classLoaderFactory = null;
	protected MarDeployer marDeployer = null;

	public GroovyDeploymentScriptHandler() {
		// todo picotize
		classLoaderFactory = new ClassLoaderFactory();
		marDeployer = new MarDeployer();
	}

	public PicoContainer handle(String context, URL url) throws DeploymentException {
		try {
			File groovyScript = new File(marDeployer
						.getWorkingDir()
						.getCanonicalPath()
						.concat("/" + context + "/composition.groovy"));

			ClassLoader classLoader = classLoaderFactory.build(context);
			GroovyClassLoader gcl = new GroovyClassLoader(classLoader);
			Class groovyClass = gcl.parseClass(groovyScript);
			DeploymentScript deployScript = (DeploymentScript)groovyClass.newInstance();
			return deployScript.build();
			
		} catch (IOException e) {
			throw new DeploymentException(e);
		} catch (CompilationFailedException e) {
			throw new DeploymentException(e);
		} catch (InstantiationException e) {
			throw new DeploymentException(e);
		} catch (IllegalAccessException e) {
			throw new DeploymentException(e);
		}
	}
}


