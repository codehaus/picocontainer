package org.picoextras.script.jython;

import org.picoextras.integrationkit.ContainerAssembler;
import org.picoextras.reflection.ReflectionContainerAdapter;
import org.picoextras.reflection.DefaultReflectionContainerAdapter;
import org.picocontainer.MutablePicoContainer;
import org.python.util.PythonInterpreter;

import java.io.Reader;
import java.io.InputStream;
import java.io.IOException;

/**
 * @author Paul Hammant
 * @author Mike Royle
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class JythonContainerAssembler implements ContainerAssembler {
    private Reader script;

    public JythonContainerAssembler(Reader script) {
        this.script = script;
    }

    public void assembleContainer(MutablePicoContainer container, Object assemblyScope) {
        PythonInterpreter interpreter = new PythonInterpreter();
        ReflectionContainerAdapter reflectionAdapter = new DefaultReflectionContainerAdapter(container);
        interpreter.set("rootContainer", reflectionAdapter);
        interpreter.exec("from org.picoextras.reflection import DefaultReflectionContainerAdapter");
        interpreter.execfile(new InputStream() {
            public int read() throws IOException {
                return script.read();
            }
        });
    }
}
