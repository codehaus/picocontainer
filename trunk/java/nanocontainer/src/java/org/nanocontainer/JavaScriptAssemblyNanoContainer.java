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
import org.nanocontainer.rhino.RhinoFrontEnd;
import org.nanocontainer.rhino.DefaultRhinoFrontEnd;

import java.io.IOException;
import java.io.Reader;

public class JavaScriptAssemblyNanoContainer extends NanoContainer {
    public JavaScriptAssemblyNanoContainer(Reader script, NanoContainerMonitor monitor) throws PicoConfigurationException, ClassNotFoundException, IOException {
        super(monitor);
        configure(script);
    }

    protected void configure(Reader script) throws IOException, ClassNotFoundException, PicoConfigurationException {

        Context cx = Context.enter();
        try {
            Scriptable scriptable = cx.initStandardObjects(null);
            try {
                ScriptableObject.defineClass(scriptable, DefaultRhinoFrontEnd.class);
            } catch (final Exception e) {
                throw new PicoConfigurationException() {
                    public String getMessage() {
                        return "JavaScriptException : " + e.getMessage();
                    }
                };
            }

            NanoHelper nanoHelper = new NanoHelper();
            Scriptable jsArgs = Context.toObject(nanoHelper, scriptable);
            scriptable.put("nano", scriptable, jsArgs);

            cx.evaluateReader(scriptable, script, "<cmd>", 1, null);

            rootContainer = nanoHelper.getRhinoFrontEnd().getPicoContainer();

        } catch (final JavaScriptException e) {
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

    public static class NanoHelper {
        RhinoFrontEnd rhinoFrontEnd;
        public RhinoFrontEnd getRhinoFrontEnd() {
            return rhinoFrontEnd;
        }
        public void setRhinoFrontEnd(RhinoFrontEnd rhinoFrontEnd) {
            this.rhinoFrontEnd = rhinoFrontEnd;
        }
    }
}
