package org.microcontainer.impl;

import org.picocontainer.PicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.ObjectReference;
import org.picocontainer.defaults.SimpleReference;
import org.microcontainer.DeploymentException;
import org.microcontainer.McaDeployer;
import org.nanocontainer.script.groovy.GroovyContainerBuilder;
import org.nanocontainer.script.ScriptedContainerBuilder;
import org.nanocontainer.reflection.DefaultSoftCompositionPicoContainer;

import java.io.FileReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Reader;

/**
 * @author Michael Ward
 */
public class GroovyDeploymentScriptHandler {
	protected ClassLoaderFactory classLoaderFactory = null;
	protected McaDeployer mcaDeployer = null;

	public GroovyDeploymentScriptHandler(McaDeployer mcaDeployer) {
		// todo picotize
		classLoaderFactory = new ClassLoaderFactory(mcaDeployer);
		this.mcaDeployer = mcaDeployer;
	}

	public PicoContainer handle(String contextName, boolean autoStart) throws DeploymentException {
		try {
			ClassLoader classLoader = classLoaderFactory.build(contextName);
			File path = new File(mcaDeployer.getWorkingDir(), contextName); // actual path to work/contextName
			FileReader script = new FileReader(new File(path, "composition.groovy"));

			// build the container from the script
			GroovyContainerBuilder gcb = new MicroGroovyContainerBuilder(script, classLoader);
			MutablePicoContainer parent = new DefaultSoftCompositionPicoContainer(classLoader);
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
    private class MicroGroovyContainerBuilder extends GroovyContainerBuilder {

        public MicroGroovyContainerBuilder(final Reader script, ClassLoader classLoader) {
            super(script, classLoader);
        }

        protected void autoStart(PicoContainer container) {
            // no
        }
    }
}


