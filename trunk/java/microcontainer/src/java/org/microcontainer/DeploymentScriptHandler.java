package org.microcontainer;

import org.picocontainer.PicoContainer;

/**
 * @author Michael Ward
 * @version $Revision$
 */
public interface DeploymentScriptHandler {

	PicoContainer handle(String contextName, boolean autoStart) throws DeploymentException;
}
