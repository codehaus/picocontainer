package org.microcontainer;

import org.picocontainer.PicoContainer;

/**
 * Defines the interface groovy deploy scripts need to implement
 */
public interface DeploymentScript {
	PicoContainer build();
}


