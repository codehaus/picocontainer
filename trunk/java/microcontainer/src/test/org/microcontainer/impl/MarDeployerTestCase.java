package org.megacontainer.impl;

import junit.framework.TestCase;

import java.io.File;
import java.net.URL;

/**
 * @author Mike Ward
 */
public class MarDeployerTestCase extends TestCase {
	public void testDeploy() throws Exception {
		MarDeployer deployer = new MarDeployer();
		File marFile = new File("test.mar");
		URL url = new URL("jar:file:" + marFile.getCanonicalPath() + "!/");
		deployer.deploy("unittest", url); // deploy the MAR to unittest context

		// assert everything is deployed to the file system correctly
		File unitTestDir = new File("work/unittest");
		assertTrue(unitTestDir.exists());
		assertTrue(new File(unitTestDir, "composition.groovy").exists());
		assertTrue(new File(unitTestDir, "MAR-INF/components/components.jar").exists());
		assertTrue(new File(unitTestDir, "MAR-INF/promoted/promoted.jar").exists());
		assertTrue(new File(unitTestDir, "MAR-INF/hidden/hidden.jar").exists());

		// cleanup, make sure it's deleted
		deployer.deleteDir(unitTestDir);
		assertFalse(new File(unitTestDir, "composition.groovy").exists());
	}
}
