package org.microcontainer.impl;

import java.io.File;

/**
 * @author Michael Ward
 */
public class DefaultConfiguration implements Configuration {

	private File workDir;
	private File tempDir;

	public DefaultConfiguration() {
		this(new File("work"), new File("temp"));
	}

	public DefaultConfiguration(File workDir, File tempDir) {
		this.workDir = workDir;
		this.tempDir = tempDir;
	}

	public File getWorkDir() {
		return workDir;
	}

	public File getTempDir() {
		return tempDir;
	}
}
