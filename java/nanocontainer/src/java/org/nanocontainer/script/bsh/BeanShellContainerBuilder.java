package org.nanocontainer.script.bsh;

import org.nanocontainer.script.ScriptedContainerBuilder;
import org.nanocontainer.integrationkit.PicoCompositionException;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;

import java.io.Reader;

import bsh.Interpreter;
import bsh.EvalError;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class BeanShellContainerBuilder extends ScriptedContainerBuilder {
    public BeanShellContainerBuilder(Reader script, ClassLoader classLoader) {
        super(script, classLoader);
    }

    protected MutablePicoContainer createContainerFromScript(PicoContainer parentContainer, Object assemblyScope) {
        Interpreter i = new Interpreter();
        try {
            i.set("parent", parentContainer);
            i.set("assemblyScope", assemblyScope);
            i.eval(script, i.getNameSpace(), "nanocontainer.bsh");
            return (MutablePicoContainer) i.get("pico");
        } catch (EvalError e) {
            throw new PicoCompositionException(e);
        }
    }
}