package org.microcontainer.impl;

import org.microcontainer.DeploymentScriptHandler;
import org.microcontainer.DeploymentException;
import org.microcontainer.ClassLoaderFactory;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.ObjectReference;
import org.picocontainer.defaults.SimpleReference;
import org.nanocontainer.script.ScriptedContainerBuilder;
import org.nanocontainer.reflection.DefaultNanoPicoContainer;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author Michael Ward
 */
public abstract class AbstractDeploymentScriptHandler implements DeploymentScriptHandler {

	protected Configuration configuration;
	protected ClassLoaderFactory classLoaderFactory;
	protected String compositionFileName;

	public AbstractDeploymentScriptHandler(Configuration configuration, ClassLoaderFactory classLoaderFactory) {
	    this.configuration = configuration;
		this.classLoaderFactory = classLoaderFactory;
	}

	public File getPath(String contextName) {
		return new File(configuration.getWorkDir(), contextName);
	}

	public FileReader getScript(File path) throws IOException  {
		return new FileReader(new File(path, compositionFileName));
	}

	public PicoContainer handle(String contextName, boolean autoStart) throws DeploymentException {
		try {
			ClassLoader classLoader = classLoaderFactory.build(contextName);
			File path = getPath(contextName);
			FileReader script = getScript(path);

			// build the container from the script
			ScriptedContainerBuilder gcb = getScriptedContainerBuilder(script, classLoader);
			DefaultNanoPicoContainer parent = new DefaultNanoPicoContainer(classLoader);
			parent.registerComponentInstance("workingDir", path);

            PicoContainer picoContainer = buildContainer(contextName, gcb, parent);
            if (autoStart) {
                picoContainer.start();
            }
            return picoContainer;

		} catch (IOException e) {
			throw new DeploymentException(e);
		}
	}

	/**
	 * responsible for actually building the container
	 */
	protected PicoContainer buildContainer(String contextName,
										   ScriptedContainerBuilder builder,
										   PicoContainer parentContainer) {

		ObjectReference containerRef = new SimpleReference();
		ObjectReference parentContainerRef = new SimpleReference();
        parentContainerRef.set(parentContainer);
        builder.buildContainer(containerRef, parentContainerRef, contextName, true);
        return (PicoContainer) containerRef.get();
    }


}
