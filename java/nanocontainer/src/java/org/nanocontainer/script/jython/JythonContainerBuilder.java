package org.nanocontainer.script.jython;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picoextras.script.ScriptedComposingLifecycleContainerBuilder;
import org.python.util.PythonInterpreter;
import org.python.core.PySystemState;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Properties;

/**
 * @author Paul Hammant
 * @author Mike Royle
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class JythonContainerBuilder extends ScriptedComposingLifecycleContainerBuilder {
    public JythonContainerBuilder(Reader script, ClassLoader classLoader) {
        super(script, classLoader);
    }

    protected MutablePicoContainer createContainer() {
// I was trying to set the custom classloader here, but it throws an exception.
//        PySystemState systemState = new PySystemState();
//        systemState.setClassLoader(classLoader);
//        PythonInterpreter interpreter = new PythonInterpreter(null, systemState);

        PythonInterpreter interpreter = new PythonInterpreter();
        interpreter.exec("from org.picocontainer.defaults import *");
        interpreter.exec("from org.picocontainer.extras import *");
        interpreter.exec("from org.picoextras.reflection import *");
        interpreter.exec("from java.net import *");
        interpreter.execfile(new InputStream() {
            public int read() throws IOException {
                return script.read();
            }
        });
        return (MutablePicoContainer) interpreter.get("pico", MutablePicoContainer.class);
    }
}
