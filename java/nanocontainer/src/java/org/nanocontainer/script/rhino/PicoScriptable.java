/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.picoextras.script.rhino;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picoextras.reflection.DefaultReflectionFrontEnd;
import org.picoextras.reflection.ReflectionFrontEnd;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

/**
 * JavaScript front-end for PicoContainer
 *
 * @author Paul Hammant
 */
public class PicoScriptable extends ScriptableObject implements Scriptable {

    private ReflectionFrontEnd reflectionFrontEnd;

    public MutablePicoContainer getPicoContainer() {
        return reflectionFrontEnd.getPicoContainer();
    }

    public String getClassName() {
        return "PicoScriptable";
    }

    public static Object jsConstructor(Context cx, Object[] args, Function ctorObj, boolean inNewExpr) {
        ReflectionFrontEnd reflectionFrontEnd = null;
        if (args.length == 1) {
            Object arg = ((NativeJavaObject) args[0]).unwrap();
            if (arg instanceof ComponentAdapterFactory) {
                reflectionFrontEnd = new DefaultReflectionFrontEnd(new DefaultPicoContainer((ComponentAdapterFactory) arg));
            } else if (arg instanceof MutablePicoContainer) {
                reflectionFrontEnd = new DefaultReflectionFrontEnd((MutablePicoContainer) arg);
            } else if (arg instanceof ReflectionFrontEnd) {
                reflectionFrontEnd = (ReflectionFrontEnd) arg;
            } else {
                List allowed = new ArrayList();
                allowed.add(ComponentAdapterFactory.class.getName());
                allowed.add(MutablePicoContainer.class.getName());
                allowed.add(ReflectionFrontEnd.class.getName());
                throw new IllegalArgumentException("Argument passed in should be one of " + allowed.toString());
            }
        } else {
            reflectionFrontEnd = new DefaultReflectionFrontEnd();
        }
        PicoScriptable rhino = new PicoScriptable();
        rhino.reflectionFrontEnd = reflectionFrontEnd;
        return rhino;
    }

    public static void jsFunction_registerComponentImplementation(Context cx, Scriptable thisObj, Object[] args, Function funObj) throws ClassNotFoundException {
        PicoScriptable rhino = (PicoScriptable) thisObj;
        if (args.length == 1) {
            rhino.reflectionFrontEnd.registerComponentImplementation((String) args[0]);
        } else if (args.length == 2) {
            rhino.reflectionFrontEnd.registerComponentImplementation((String) args[0], (String) args[1]);
        }
    }

    public static void jsFunction_registerComponentInstance(Context cx, Scriptable thisObj, Object[] args, Function funObj) throws ClassNotFoundException {
        PicoScriptable rhino = (PicoScriptable) thisObj;
        MutablePicoContainer picoContainer = rhino.reflectionFrontEnd.getPicoContainer();
        if (args.length == 1) {
            picoContainer.registerComponentInstance(((NativeJavaObject) args[0]).unwrap());
        } else if (args.length == 2) {
            picoContainer.registerComponentInstance(args[0], ((NativeJavaObject) args[1]).unwrap());
        }
    }

    public static void jsFunction_addParent(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
        PicoScriptable child = (PicoScriptable) thisObj;
        MutablePicoContainer childContainer = child.reflectionFrontEnd.getPicoContainer();

        PicoScriptable parent = (PicoScriptable) args[0];
        MutablePicoContainer parentContainer = parent.reflectionFrontEnd.getPicoContainer();
        childContainer.addParent(parentContainer);
    }

    public static void jsFunction_addFileClassPathJar(Context cx, Scriptable thisObj, Object[] args, Function funObj) throws IOException {
        PicoScriptable rhino = (PicoScriptable) thisObj;
        ReflectionFrontEnd rfe = rhino.reflectionFrontEnd;
        String path = (String) args[0];
        File file = new File(path);
        if (!file.exists()) {
            throw new IOException(file.getAbsolutePath() + " doesn't exist");
        }
        rfe.addClassLoaderURL(file.toURL());
    }

}
