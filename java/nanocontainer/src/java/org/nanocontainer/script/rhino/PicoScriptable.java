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
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picoextras.reflection.DefaultReflectionFrontEnd;
import org.picoextras.reflection.ReflectionFrontEnd;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * JavaScript front-end for PicoContainer
 *
 * @author Paul Hammant
 */
public class PicoScriptable extends ScriptableObject implements Scriptable {

    private ReflectionFrontEnd reflectionFrontEnd;
    static MutablePicoContainer picoContainer;

    public PicoScriptable(PicoScriptable parent) {
        reflectionFrontEnd = new DefaultReflectionFrontEnd(parent.reflectionFrontEnd);
    }

    public PicoScriptable() {
    }

    public String getClassName() {
        return "PicoScriptable";
    }

    public static Object jsConstructor(Context cx, Object[] args, Function ctorObj, boolean inNewExpr) {
        PicoScriptable picoScriptable;
        if (args.length == 1) {
            if(args[0] instanceof PicoScriptable) {
                picoScriptable = new PicoScriptable((PicoScriptable)args[0]);
            } else {
                picoScriptable = new PicoScriptable();
                Object arg = ((NativeJavaObject) args[0]).unwrap();
                if (arg instanceof MutablePicoContainer) {
                    picoScriptable.reflectionFrontEnd = new DefaultReflectionFrontEnd(new DefaultPicoContainer((ComponentAdapterFactory) arg));
                } else if (arg instanceof MutablePicoContainer) {
                    picoScriptable.reflectionFrontEnd = new DefaultReflectionFrontEnd((MutablePicoContainer) arg);
                } else if (arg instanceof ReflectionFrontEnd) {
                    picoScriptable.reflectionFrontEnd = (ReflectionFrontEnd) arg;
                } else {
                    List allowed = new ArrayList();
                    allowed.add(ComponentAdapterFactory.class.getName());
                    allowed.add(MutablePicoContainer.class.getName());
                    allowed.add(ReflectionFrontEnd.class.getName());
                    throw new IllegalArgumentException("Argument passed in should be one of " + allowed.toString());
                }
            }
        } else {
            picoScriptable = new PicoScriptable();
            picoScriptable.reflectionFrontEnd = new DefaultReflectionFrontEnd(picoContainer);
        }
        return picoScriptable;
    }

    public static void jsFunction_registerComponentImplementation(Context cx, Scriptable thisObj, Object[] args, Function funObj) throws ClassNotFoundException {
        PicoScriptable picoScriptable = (PicoScriptable) thisObj;
        if (args.length == 1) {
            picoScriptable.reflectionFrontEnd.registerComponentImplementation((String) args[0]);
        } else if (args.length == 2) {
            picoScriptable.reflectionFrontEnd.registerComponentImplementation((String) args[0], (String) args[1]);
        }
    }

    public static void jsFunction_registerComponentInstance(Context cx, Scriptable thisObj, Object[] args, Function funObj) throws ClassNotFoundException {
        PicoScriptable picoScriptable = (PicoScriptable) thisObj;
        MutablePicoContainer picoContainer = picoScriptable.reflectionFrontEnd.getPicoContainer();
        if (args.length == 1) {
            picoContainer.registerComponentInstance(((NativeJavaObject) args[0]).unwrap());
        } else if (args.length == 2) {
            Object instance;
            if(args[1] instanceof NativeJavaObject) {
                instance = ((NativeJavaObject) args[1]).unwrap();
            } else {
                instance = args[1];
            }
            if(instance instanceof PicoScriptable) {
                instance = ((PicoScriptable)instance).getPicoContainer();
            }
            picoContainer.registerComponentInstance(args[0], instance);
        }
    }

    private PicoContainer getPicoContainer() {
        return reflectionFrontEnd.getPicoContainer();
    }

    public static void jsFunction_setParent(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
        PicoScriptable child = (PicoScriptable) thisObj;
        MutablePicoContainer childContainer = child.reflectionFrontEnd.getPicoContainer();

        PicoScriptable parent = (PicoScriptable) args[0];
        MutablePicoContainer parentContainer = parent.reflectionFrontEnd.getPicoContainer();
        childContainer.setParent(parentContainer);
    }

    public static void jsFunction_addFileClassPathJar(Context cx, Scriptable thisObj, Object[] args, Function funObj) throws IOException {
        PicoScriptable picoScriptable = (PicoScriptable) thisObj;
        String path = (String) args[0];
        File file = new File(path);
        if (!file.exists()) {
            throw new IOException(file.getAbsolutePath() + " doesn't exist");
        }
        picoScriptable.reflectionFrontEnd.addClassLoaderURL(file.toURL());
    }

}
