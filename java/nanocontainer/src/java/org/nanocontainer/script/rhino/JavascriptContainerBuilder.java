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
import org.nanocontainer.script.ScriptedComposingLifecycleContainerBuilder;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.nanocontainer.integrationkit.PicoAssemblyException;

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
public class JavascriptContainerBuilder extends ScriptedComposingLifecycleContainerBuilder {
    public JavascriptContainerBuilder(Reader script, ClassLoader classLoader) {
        super(script, classLoader);
    }

    protected MutablePicoContainer createContainer(PicoContainer parentContainer) {
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
                throw new PicoAssemblyException("The script must define a variable named 'pico'");
            }
            if (!(pico instanceof NativeJavaObject)) {
                throw new PicoAssemblyException("The 'pico' variable must be of type " + MutablePicoContainer.class.getName());
            }
            Object javaObject = ((NativeJavaObject) pico).unwrap();
            if (!(javaObject instanceof MutablePicoContainer)) {
                throw new PicoAssemblyException("The 'pico' variable must be of type " + MutablePicoContainer.class.getName());
            }
            return (MutablePicoContainer) javaObject;
        } catch (PicoAssemblyException e) {
            throw e;
        } catch (JavaScriptException e) {
            Object value = e.getValue();
            if(value instanceof Throwable) {
                throw new PicoAssemblyException((Throwable) value);
            } else {
                throw new PicoAssemblyException(e);
            }
        } catch (Exception e) {
            throw new PicoAssemblyException(e);
        } finally {
            Context.exit();
        }
    }
}
