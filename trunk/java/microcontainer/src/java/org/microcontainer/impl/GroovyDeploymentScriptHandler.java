package org.microcontainer.impl;

import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.ObjectReference;
import org.picocontainer.defaults.SimpleReference;
import org.microcontainer.DeploymentException;
import org.microcontainer.DeploymentScriptHandler;
import org.microcontainer.ClassLoaderFactory;
import org.nanocontainer.script.groovy.GroovyContainerBuilder;
import org.nanocontainer.script.ScriptedContainerBuilder;
import org.nanocontainer.reflection.DefaultNanoPicoContainer;

import java.io.FileReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Reader;

/**
 * @author Michael Ward
 * @version $Revision$
 */
public class GroovyDeploymentScriptHandler implements DeploymentScriptHandler {
	protected ClassLoaderFactory classLoaderFactory = null;
	protected File workingDir = null;

	public GroovyDeploymentScriptHandler(ClassLoaderFactory classLoaderFactory, File workingDir) {
		this.classLoaderFactory = classLoaderFactory;
		this.workingDir = workingDir;
	}

	public PicoContainer handle(String contextName, boolean autoStart) throws DeploymentException {
		try {
			ClassLoader classLoader = classLoaderFactory.build(contextName);
			File path = new File(workingDir, contextName); // actual path to work/contextName
			FileReader script = new FileReader(new File(path, "composition.groovy"));

			// build the container from the script
			GroovyContainerBuilder gcb = new MicroGroovyContainerBuilder(script, classLoader);
			DefaultNanoPicoContainer parent = new DefaultNanoPicoContainer(classLoader);
			parent.registerComponentInstance("workingDir", path);

            PicoContainer picoContainer = buildContainer(contextName, gcb, parent);
            if (autoStart) {
                picoContainer.start();
            }
            return picoContainer;
			
		} catch (FileNotFoundException e) {
			throw new DeploymentException(e);
		}
	}

	/**
	 * responsible for actually building the container
	 */
	protected PicoContainer buildContainer(String contextName, ScriptedContainerBuilder builder, PicoContainer parentContainer) {
		ObjectReference containerRef = new SimpleReference();
		ObjectReference parentContainerRef = new SimpleReference();
        parentContainerRef.set(parentContainer);
        builder.buildContainer(containerRef, parentContainerRef, contextName, true);
        return (PicoContainer) containerRef.get();
    }

	/**
	 * This class is necessary to prevent autoStarting of microcontainer
	 */
    private class MicroGroovyContainerBuilder extends GroovyContainerBuilder {

        public MicroGroovyContainerBuilder(final Reader script, ClassLoader classLoader) {
            super(script, classLoader);
        }

        protected void autoStart(PicoContainer container) {
            // no
        }
    }
}


