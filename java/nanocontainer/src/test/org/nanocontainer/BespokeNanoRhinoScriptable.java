/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/

package org.nanocontainer;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.nanocontainer.rhino.DefaultNanoRhinoScriptable;


public class BespokeNanoRhinoScriptable extends DefaultNanoRhinoScriptable {
    public static boolean used;

    public BespokeNanoRhinoScriptable() {
        used = true;
    }

    public static Object jsConstructor(Context cx, Object[] args, Function ctorObj, boolean inNewExpr) {
        return DefaultNanoRhinoScriptable.jsConstructor(cx, args, ctorObj, inNewExpr);
    }

    public static void jsFunction_addComponent(Context cx, Scriptable thisObj, Object[] args, Function funObj)
            throws ClassNotFoundException {
        DefaultNanoRhinoScriptable.jsFunction_addComponent(cx, thisObj, args, funObj);
    }

    public static void jsFunction_addComponentWithClassKey(Context cx, Scriptable thisObj, Object[] args, Function funObj)
            throws ClassNotFoundException {
        DefaultNanoRhinoScriptable.jsFunction_addComponentWithClassKey(cx, thisObj, args, funObj);
    }

    public static void jsFunction_addContainer(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
        DefaultNanoRhinoScriptable.jsFunction_addContainer(cx, thisObj, args, funObj);
    }

}
