/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
package org.nanocontainer.script.groovy;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import org.nanocontainer.integrationkit.PicoAssemblyException;
import org.nanocontainer.script.ScriptedComposingLifecycleContainerBuilder;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

/**
 * {@inheritDoc}
 * The script has to return an instance of {@link PicoContainer}.
 * There is an implicit variable named "parent" that may contain a reference to a parent
 * container. It is recommended to use this as a constructor argument to the instantiated
 * PicoContainer.
 *
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class GroovyContainerBuilder extends ScriptedComposingLifecycleContainerBuilder {
    public GroovyContainerBuilder(Reader script, ClassLoader classLoader) {
        super(script, classLoader);
    }

    protected MutablePicoContainer createContainer(PicoContainer parentContainer) {
        Object result = null;
        try {
            GroovyClassLoader loader = new GroovyClassLoader(classLoader);
            Class groovyClass = loader.parseClass(new InputStream() {
                public int read() throws IOException {
                    return script.read();
                }
            }, "picocontainer.groovy");

            GroovyObject groovyObject = (GroovyObject) groovyClass.newInstance();
            Object[] args = {parentContainer};
            result = groovyObject.invokeMethod("buildContainer", args);
        } catch (Exception e) {
            throw new PicoAssemblyException(e);
        }
        if (result instanceof MutablePicoContainer) {
            return (MutablePicoContainer) result;
        } else {
            throw new PicoAssemblyException("The script didn't return an instance of " + MutablePicoContainer.class.getName());
        }
    }
}