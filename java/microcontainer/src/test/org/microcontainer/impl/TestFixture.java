package org.microcontainer.impl;

import org.microcontainer.Kernel;
import org.microcontainer.McaDeployer;
import org.microcontainer.DeploymentScriptHandler;
import org.microcontainer.ClassLoaderFactory;

import java.io.File;

/**
 * @author Michael Ward
 */
public class TestFixture {
	public static File workingDir = new File("work");
	public static File tempDir = new File("temp");

	public static Kernel createKernel() {
		McaDeployer deployer = createDefaultMcaDeployer();
		ClassLoaderFactory classLoaderFactory = new DefaultClassLoaderFactory(workingDir);
		DeploymentScriptHandler scriptHandler = new GroovyDeploymentScriptHandler(classLoaderFactory, workingDir);

        return new DefaultKernel( deployer, scriptHandler);
	}

	public static ClassLoaderFactory createClassLoaderFactory() {
		return new DefaultClassLoaderFactory(workingDir);
	}

	public static DefaultMcaDeployer createDefaultMcaDeployer() {
		return new DefaultMcaDeployer(workingDir, tempDir);
	}
}
