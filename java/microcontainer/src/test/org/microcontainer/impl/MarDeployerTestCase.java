package org.microcontainer.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.net.URL;
import java.net.HttpURLConnection;

import junit.framework.TestCase;

/**
 * @author Mike Ward
 */
public class MarDeployerTestCase extends TestCase {
    private MarDeployer deployer;
	private File unitTestDir;

	protected void setUp() throws Exception {
		deployer = new MarDeployer();
		unitTestDir = new File("work/unittest");
	}

	protected void tearDown() throws Exception {
		// cleanup, make sure folder and content has been deleted
		deployer.deleteDir(unitTestDir);
		assertFalse(new File(unitTestDir, "composition.groovy").exists());
	}

	public void testDeploy() throws Exception {
		File marFile = new File("test.mar");
		URL url = new URL("jar:file:" + marFile.getCanonicalPath() + "!/");
		deployer.deploy("unittest", url); // deploy the MAR to unittest context

		validateMarDeployedToWorkingDir();
	}

	public void testDeployFromHttp() throws Exception {
		// normally this directory would be created by deploy()... but we are bypassing for testing
		File unitTestDir = new File("work/unittest");
		unitTestDir.mkdir();

		HttpURLConnection connection = new MockHttpURLConnection(null);
		MarDeployer marDeployer = new MarDeployer();
		marDeployer.handleRemoteMAR(unitTestDir, connection);

		validateMarDeployedToWorkingDir();

		// cleanup temp dir
		File tempDir = new File("temp");
		assertTrue(tempDir.exists());
		deployer.deleteDir(tempDir);
		assertFalse(tempDir.exists());
	}

	protected void validateMarDeployedToWorkingDir() {
		// assert everything is deployed to the file system correctly
		assertTrue(unitTestDir.exists());
		assertTrue(new File(unitTestDir, "composition.groovy").exists());
		assertTrue(new File(unitTestDir, "MAR-INF/components/components.jar").exists());
		assertTrue(new File(unitTestDir, "MAR-INF/promoted/promoted.jar").exists());
		assertTrue(new File(unitTestDir, "MAR-INF/hidden/hidden.jar").exists());
	}

	protected class MockHttpURLConnection extends HttpURLConnection {
		public MockHttpURLConnection(URL u) {
			super(u);
		}

		public void disconnect() {
		}

		public boolean usingProxy() {
			return false;
		}

		public void connect() throws IOException {
		}

		public InputStream getInputStream() throws IOException {
			File marFile = new File("test.mar");
			return new FileInputStream(marFile);
		}
	}
}
