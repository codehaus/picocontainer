/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.script.rhino;

import java.io.IOException;
import java.io.Reader;
import java.net.URL;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.DefiningClassLoader;
import org.mozilla.javascript.GeneratedClassLoader;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.Scriptable;
import org.picocontainer.PicoContainer;
import org.picocontainer.script.LifecycleMode;
import org.picocontainer.script.ScriptedPicoContainerMarkupException;
import org.picocontainer.script.ScriptedContainerBuilder;


/**
 * {@inheritDoc}
 * The script has to assign a "pico" variable with an instance of
 * {@link PicoContainer}.
 * There is an implicit variable named "parent" that may contain a reference to a parent
 * container. It is recommended to use this as a constructor argument to the instantiated
 * PicoContainer.
 *
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 * @author Mauro Talevi
 */
public class JavascriptContainerBuilder extends ScriptedContainerBuilder {

    public JavascriptContainerBuilder(Reader script, ClassLoader classLoader) {
    	this(script,classLoader, LifecycleMode.AUTO_LIFECYCLE);
    }
	
    public JavascriptContainerBuilder(Reader script, ClassLoader classLoader, LifecycleMode lifecycleMode) {
        super(script, classLoader, lifecycleMode);
    }

    public JavascriptContainerBuilder(URL script, ClassLoader classLoader) {
    	this(script,classLoader, LifecycleMode.AUTO_LIFECYCLE);
    }
    
    public JavascriptContainerBuilder(URL script, ClassLoader classLoader, LifecycleMode lifecycleMode) {
        super(script, classLoader, lifecycleMode);
    }

    protected PicoContainer createContainerFromScript(PicoContainer parentContainer, Object assemblyScope) {
        final ClassLoader loader = getClassLoader();
        Context cx = new Context() {
            public GeneratedClassLoader createClassLoader(ClassLoader parent) {
                return new DefiningClassLoader(loader);
            }
        };
        cx = Context.enter(cx);

        try {
        	
            Scriptable scope = new ImporterTopLevel(cx);
            scope.put("parent", scope, parentContainer);
            scope.put("assemblyScope", scope, assemblyScope);
            cx.evaluateReader(scope, getScriptReader(), "picocontainer.js", 1, null);
            Object pico = scope.get("pico", scope);

            if (pico == null) {
                throw new ScriptedPicoContainerMarkupException("The script must define a variable named 'pico'");
            }
            if (!(pico instanceof NativeJavaObject)) {
                throw new ScriptedPicoContainerMarkupException("The 'pico' variable must be of type " + NativeJavaObject.class.getName());
            }
            Object javaObject = ((NativeJavaObject) pico).unwrap();
            if (!(javaObject instanceof PicoContainer)) {
                throw new ScriptedPicoContainerMarkupException("The 'pico' variable must be of type " + PicoContainer.class.getName());
            }
            return (PicoContainer) javaObject;
        } catch (ScriptedPicoContainerMarkupException e) {
            throw e;
        } catch (RhinoException e) {
			StringBuilder message = new StringBuilder();
			message.append("There was an error in script '");
			message.append(e.sourceName());
			message.append("'.  Line number: ");
			message.append(e.lineNumber());
			message.append(" and Column number: ");
			message.append(e.columnNumber());
			message.append(" .");
			throw new ScriptedPicoContainerMarkupException(message.toString(), e);
        } catch (IOException e) {
            throw new ScriptedPicoContainerMarkupException("IOException encountered, message -'" + e.getMessage() + "'", e);
        } finally {
            Context.exit();
        }
    }
}
