package org.microcontainer.impl;

import org.picocontainer.PicoContainer;
import org.microcontainer.ClassLoaderFactory;
import org.nanocontainer.script.groovy.GroovyContainerBuilder;
import org.nanocontainer.script.ScriptedContainerBuilder;

import java.io.FileReader;
import java.io.File;
import java.io.Reader;

/**
 * @author Michael Ward
 * @version $Revision$
 */
public class GroovyDeploymentScriptHandler extends AbstractDeploymentScriptHandler {

	public GroovyDeploymentScriptHandler(ClassLoaderFactory classLoaderFactory, File workingDir) {
		super(classLoaderFactory, workingDir);
		compositionFileName = "composition.groovy";
	}

	public ScriptedContainerBuilder getScriptedContainerBuilder(FileReader script, ClassLoader classLoader) {
		return new MicroGroovyContainerBuilder(script, classLoader);
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


