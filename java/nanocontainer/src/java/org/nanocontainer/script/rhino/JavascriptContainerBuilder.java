/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.nanocontainer.script.rhino;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.DefiningClassLoader;
import org.mozilla.javascript.GeneratedClassLoader;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.NativeJavaPackage;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.nanocontainer.script.ScriptedContainerBuilder;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.nanocontainer.integrationkit.PicoCompositionException;

import java.io.Reader;

/**
 * {@inheritDoc}
 * The script has to assign a "pico" variable with an instance of {@link PicoContainer}.
 * There is an implicit variable named "parent" that may contain a reference to a parent
 * container. It is recommended to use this as a constructor argument to the instantiated
 * PicoContainer.
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 */
public class JavascriptContainerBuilder extends ScriptedContainerBuilder {
    public JavascriptContainerBuilder(Reader script, ClassLoader classLoader) {
        super(script, classLoader);
    }

    protected MutablePicoContainer createContainerFromScript(PicoContainer parentContainer, Object assemblyScope) {
        Context cx = new Context() {
            public GeneratedClassLoader createClassLoader(ClassLoader parent) {
                return new DefiningClassLoader(classLoader) {
                };
            }
        };
        cx = Context.enter(cx);

        try {
            Scriptable scope = new ImporterTopLevel(cx);
            scope.put("parent", scope, parentContainer);
            scope.put("assemblyScope", scope, assemblyScope);
            ImporterTopLevel.importPackage(cx,
                    scope, new NativeJavaPackage[]{
                        new NativeJavaPackage("org.picocontainer.defaults", classLoader),
                        new NativeJavaPackage("org.nanocontainer.reflection", classLoader),
                        // File, URL and URLClassLoader will be frequently used by scripts.
                        new NativeJavaPackage("java.net", classLoader),
                        new NativeJavaPackage("java.io", classLoader),
                    },
                    null);
            Script scriptObject = cx.compileReader(scope, script, "javascript", 1, null);
            scriptObject.exec(cx, scope);
            Object pico = scope.get("pico", scope);

            if (pico == null) {
                throw new PicoCompositionException("The script must define a variable named 'pico'");
            }
            if (!(pico instanceof NativeJavaObject)) {
                throw new PicoCompositionException("The 'pico' variable must be of type " + MutablePicoContainer.class.getName());
            }
            Object javaObject = ((NativeJavaObject) pico).unwrap();
            if (!(javaObject instanceof MutablePicoContainer)) {
                throw new PicoCompositionException("The 'pico' variable must be of type " + MutablePicoContainer.class.getName());
            }
            return (MutablePicoContainer) javaObject;
        } catch (PicoCompositionException e) {
            throw e;
        } catch (JavaScriptException e) {
            Object value = e.getValue();
            if(value instanceof Throwable) {
                throw new PicoCompositionException((Throwable) value);
            } else {
                throw new PicoCompositionException(e);
            }
        } catch (Exception e) {
            throw new PicoCompositionException(e);
        } finally {
            Context.exit();
        }
    }
}
