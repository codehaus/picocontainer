/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.rhino;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.nanocontainer.reflection.DefaultReflectionFrontEnd;
import org.nanocontainer.reflection.ReflectionFrontEnd;
import org.picocontainer.MutablePicoContainer;

public class DefaultRhinoFrontEnd extends ScriptableObject implements RhinoFrontEnd {

    ReflectionFrontEnd reflectionFrontEnd = new DefaultReflectionFrontEnd();
    public DefaultRhinoFrontEnd() {

    }

    public MutablePicoContainer getPicoContainer() {
        return reflectionFrontEnd.getPicoContainer();
    }

    public String getClassName() {
        return "RhinoFrontEnd";
    }

    public static void jsFunction_addComponent(Context cx, Scriptable thisObj,Object[] args, Function funObj) throws ClassNotFoundException {
        DefaultRhinoFrontEnd rhino = (DefaultRhinoFrontEnd) thisObj;
        String componentClass = (String) args[0];
        rhino.reflectionFrontEnd.registerComponentImplementation(componentClass);
    }

    public static void jsFunction_addContainer(Context cx, Scriptable thisObj,Object[] args, Function funObj) {
        DefaultRhinoFrontEnd parent = (DefaultRhinoFrontEnd) thisObj;
        DefaultRhinoFrontEnd child = (DefaultRhinoFrontEnd) args[0];
        parent.reflectionFrontEnd.getPicoContainer().addChild(child.reflectionFrontEnd.getPicoContainer());

    }
}
