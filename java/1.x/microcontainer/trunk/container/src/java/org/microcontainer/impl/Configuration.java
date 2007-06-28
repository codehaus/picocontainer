package org.microcontainer.impl;

import java.io.File;

/**
 * Any configurable parameter for MicroContainer should be added to the configuration
 *
 * This simplified construction of an instance of MicroContainer
 *
 * @author Michael Ward
 */
public interface Configuration {

	File getWorkDir();

	File getTempDir();
}
