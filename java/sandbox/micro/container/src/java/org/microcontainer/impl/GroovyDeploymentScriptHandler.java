package org.microcontainer.impl;

import groovy.lang.Binding;

import java.io.FileReader;
import java.io.Reader;

import org.microcontainer.ClassLoaderFactory;
import org.microcontainer.jmx.JmxDecorationDelegate;
import org.nanocontainer.script.ScriptedContainerBuilder;
import org.nanocontainer.script.groovy.GroovyContainerBuilder;
import org.nanocontainer.script.groovy.OldGroovyNodeBuilder;
import org.picocontainer.PicoContainer;

/**
 * @author Michael Ward
 * @version $Revision$
 */
public class GroovyDeploymentScriptHandler extends AbstractDeploymentScriptHandler {

	public GroovyDeploymentScriptHandler(Configuration configuration, ClassLoaderFactory classLoaderFactory) {
		super(configuration, classLoaderFactory);
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

		/**
		 * Let's automatically register the builder to Groovy Binding so it's available in the script
		 */
		protected void handleBinding(Binding binding) {
     		super.handleBinding(binding);

			OldGroovyNodeBuilder builder = new OldGroovyNodeBuilder(new JmxDecorationDelegate());
			binding.setVariable("builder", builder);
		}

        protected void autoStart(PicoContainer container) {
            // no
        }
    }
}


