/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer;

import org.mozilla.javascript.*;
import org.picocontainer.PicoConfigurationException;
import org.nanocontainer.rhino.NanoRhinoScriptable;
import org.nanocontainer.rhino.DefaultNanoRhinoScriptable;

import java.io.IOException;
import java.io.Reader;

public class JavaScriptAssemblyNanoContainer extends NanoContainer {

    private Scriptable scriptable;
    private Class nanoRhinoScriptableClass;

    public JavaScriptAssemblyNanoContainer(Reader script, NanoContainerMonitor monitor, Class nanoRhinoScriptableClass) throws PicoConfigurationException, ClassNotFoundException, IOException {
        super(monitor);
        this.nanoRhinoScriptableClass = nanoRhinoScriptableClass;
        configure(script);
    }


    public JavaScriptAssemblyNanoContainer(Reader script, NanoContainerMonitor monitor) throws PicoConfigurationException, ClassNotFoundException, IOException {
        super(monitor);
        configure(script);
    }

    protected void configure(Reader script) throws IOException, ClassNotFoundException, PicoConfigurationException {

        Context cx = Context.enter();
        try {
            scriptable = cx.initStandardObjects(null);

            if (nanoRhinoScriptableClass != null) {
                defineClass(nanoRhinoScriptableClass);
            } else {
                defineClass(DefaultNanoRhinoScriptable.class);                
            }

            NanoHelper nanoHelper = new NanoHelper();
            Scriptable jsArgs = Context.toObject(nanoHelper, scriptable);
            scriptable.put("nano", scriptable, jsArgs);

            cx.evaluateReader(scriptable, script, "<cmd>", 1, null);

            rootContainer = nanoHelper.getNanoRhinoScriptable().getPicoContainer();

        } catch (final JavaScriptException e) {
            e.printStackTrace();
            throw new PicoConfigurationException() {
                public String getMessage() {
                    return "JavaScriptException : " + e.getMessage();
                }
            };
        } finally {
            Context.exit();
        }
        instantiateComponentsBreadthFirst(rootContainer);
        startComponentsBreadthFirst();
    }

    private void defineClass(Class rhinoClass) throws PicoConfigurationException {
        try {
            ScriptableObject.defineClass(scriptable, rhinoClass);
        } catch (final Exception e) {
            throw new PicoConfigurationException() {
                public String getMessage() {
                    return "JavaScriptException : " + e.getMessage();
                }
            };
        }
    }

    public static class NanoHelper {
        NanoRhinoScriptable rhinoFrontEnd;
        public NanoRhinoScriptable getNanoRhinoScriptable() {
            return rhinoFrontEnd;
        }
        public void setNanoRhinoScriptable(NanoRhinoScriptable rhinoFrontEnd) {
            this.rhinoFrontEnd = rhinoFrontEnd;
        }
    }
}
