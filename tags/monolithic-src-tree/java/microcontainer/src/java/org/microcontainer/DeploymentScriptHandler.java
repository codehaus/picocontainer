package org.microcontainer;

import org.picocontainer.PicoContainer;
import org.nanocontainer.script.ScriptedContainerBuilder;

import java.io.FileReader;
import java.io.File;
import java.io.IOException;

/**
 * @author Michael Ward
 * @version $Revision$
 */
public interface DeploymentScriptHandler {

	PicoContainer handle(String contextName, boolean autoStart) throws DeploymentException;

	File getPath(String contextName);

	FileReader getScript(File path) throws IOException;

	ScriptedContainerBuilder getScriptedContainerBuilder(FileReader script, ClassLoader classLoader);
}
