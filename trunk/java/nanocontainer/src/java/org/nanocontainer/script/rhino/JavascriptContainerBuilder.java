package org.picoextras.script.rhino;

import org.mozilla.javascript.*;
import org.picocontainer.MutablePicoContainer;
import org.picoextras.integrationkit.PicoAssemblyException;
import org.picoextras.script.ScriptedComposingLifecycleContainerBuilder;

import java.io.Reader;

/**
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 */
public class JavascriptContainerBuilder extends ScriptedComposingLifecycleContainerBuilder {
    public JavascriptContainerBuilder(Reader script, ClassLoader classLoader) {
        super(script, classLoader);
    }

    protected MutablePicoContainer createContainer() {
        Context cx = Context.enter();
        try {
            Scriptable scope = new ImporterTopLevel(cx);
            ImporterTopLevel.importPackage(cx,
                    scope, new NativeJavaPackage[]{
                        new NativeJavaPackage("org.picocontainer.defaults", classLoader),
                        new NativeJavaPackage("org.picocontainer.extras", classLoader),
                        new NativeJavaPackage("org.picoextras.reflection", classLoader),
                        // File, URL and URLClassLoader will be frequently used by scripts.
                        new NativeJavaPackage("java.net", classLoader),
                        new NativeJavaPackage("java.io", classLoader)
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
