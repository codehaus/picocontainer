/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
package org.nanocontainer.script.groovy;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.codehaus.groovy.syntax.SyntaxException;
import org.nanocontainer.script.ScriptedComposingLifecycleContainerBuilder;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picoextras.integrationkit.PicoAssemblyException;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

/**
 * {@inheritDoc}
 * The script has to return an instance of {@link PicoContainer}.
 * There is an implicit variable named "parent" that may contain a reference to a parent
 * container. It is recommended to use this as a constructor argument to the instantiated
 * PicoContainer.
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class GroovyContainerBuilder extends ScriptedComposingLifecycleContainerBuilder {
    public GroovyContainerBuilder(Reader script, ClassLoader classLoader) {
        super(script, classLoader);
    }

    protected MutablePicoContainer createContainer(PicoContainer parentContainer) {
        Binding binding = new Binding();
        binding.setVariable("parent", parentContainer);
        GroovyShell shell = new GroovyShell(binding);
        try {
// * imports not supported by groovy yet
// http://lists.codehaus.org/pipermail/groovy-user/2004q1/000164.html
//            shell.evaluate("import org.picocontainer.*");
//            shell.evaluate("import org.picocontainer.defaults.*");
            return (MutablePicoContainer) shell.evaluate(new InputStream() {
                public int read() throws IOException {
                    return script.read();
                }
            }, "pico.groovy");
        } catch (SyntaxException e) {
            throw new PicoAssemblyException(e);
        } catch (ClassNotFoundException e) {
            throw new PicoAssemblyException(e);
        } catch (IOException e) {
            throw new PicoAssemblyException(e);
        }
    }
}