package org.picoextras.script;

import org.picoextras.integrationkit.ContainerAssembler;
import org.picoextras.reflection.ReflectionFrontEnd;
import org.picoextras.reflection.DefaultReflectionFrontEnd;
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
        ReflectionFrontEnd reflectionAdapter = new DefaultReflectionFrontEnd(container);
        interpreter.set("rootContainer", reflectionAdapter);
        interpreter.exec("from org.picoextras.reflection import DefaultReflectionFrontEnd");
        interpreter.execfile(new InputStream() {
            public int read() throws IOException {
                return script.read();
            }
        });
    }
}
