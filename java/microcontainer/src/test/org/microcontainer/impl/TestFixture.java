package org.microcontainer.impl;

import org.microcontainer.Kernel;
import org.microcontainer.McaDeployer;
import org.microcontainer.DeploymentScriptHandler;

/**
 * @author Michael Ward
 */
public class TestFixture {

	public static Kernel createKernel() {
		McaDeployer deployer = new DefaultMcaDeployer();
		DeploymentScriptHandler scriptHandler = new GroovyDeploymentScriptHandler(deployer);

        return new DefaultKernel( deployer, scriptHandler);     		
	}
}
