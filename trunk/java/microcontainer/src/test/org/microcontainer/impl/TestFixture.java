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
	private static File workingDir = new File("work");
	private static File tempDir = new File("temp");

	public static Kernel createKernel() {
		McaDeployer deployer = createDefaultMcaDeployer();
		ClassLoaderFactory classLoaderFactory = new DefaultClassLoaderFactory(deployer);
		DeploymentScriptHandler scriptHandler = new GroovyDeploymentScriptHandler(classLoaderFactory, workingDir);

        return new DefaultKernel( deployer, scriptHandler);
	}

	public static ClassLoaderFactory createClassLoaderFactory() {
		McaDeployer deployer = createDefaultMcaDeployer();
		return new DefaultClassLoaderFactory(deployer);
	}

	public static DefaultMcaDeployer createDefaultMcaDeployer() {
		return new DefaultMcaDeployer(workingDir, tempDir);
	}
}
