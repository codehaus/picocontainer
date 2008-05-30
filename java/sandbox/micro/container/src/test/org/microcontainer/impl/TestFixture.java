package org.microcontainer.impl;

import org.microcontainer.Kernel;
import org.microcontainer.McaDeployer;
import org.microcontainer.DeploymentScriptHandler;
import org.microcontainer.ClassLoaderFactory;

/**
 * @author Michael Ward
 */
public class TestFixture {
	private static Configuration configuration = new DefaultConfiguration();

	public static Kernel createKernel() {
		McaDeployer deployer = createDefaultMcaDeployer();
		ClassLoaderFactory classLoaderFactory = new DefaultClassLoaderFactory(configuration);
		DeploymentScriptHandler scriptHandler = new GroovyDeploymentScriptHandler(configuration, classLoaderFactory);

        return new DefaultKernel( deployer, scriptHandler);
	}

	public static ClassLoaderFactory createClassLoaderFactory() {
		return new DefaultClassLoaderFactory(configuration);
	}

	public static DefaultMcaDeployer createDefaultMcaDeployer() {
		return new DefaultMcaDeployer(configuration);
	}
}
