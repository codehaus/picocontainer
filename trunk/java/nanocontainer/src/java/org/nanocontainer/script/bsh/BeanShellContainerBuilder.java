package org.nanocontainer.script.bsh;

import bsh.EvalError;
import bsh.Interpreter;
import org.nanocontainer.integrationkit.PicoCompositionException;
import org.nanocontainer.script.ScriptedContainerBuilder;
import org.picocontainer.PicoContainer;

import java.io.Reader;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class BeanShellContainerBuilder extends ScriptedContainerBuilder {
    public BeanShellContainerBuilder(Reader script, ClassLoader classLoader) {
        super(script, classLoader);
    }

    protected PicoContainer createContainerFromScript(PicoContainer parentContainer, Object assemblyScope) {
        Interpreter i = new Interpreter();
        try {
            i.set("parent", parentContainer);
            i.set("assemblyScope", assemblyScope);
            i.eval(script, i.getNameSpace(), "nanocontainer.bsh");
            return (PicoContainer) i.get("pico");
        } catch (EvalError e) {
            throw new PicoCompositionException(e);
        }
    }
}