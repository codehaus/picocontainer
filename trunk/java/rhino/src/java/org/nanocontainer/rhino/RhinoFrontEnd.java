package org.nanocontainer.rhino;

import org.mozilla.javascript.*;
import org.nanocontainer.reflection.ReflectionFrontEnd;
import org.nanocontainer.reflection.DefaultReflectionFrontEnd;

public class RhinoFrontEnd extends ScriptableObject {

    ReflectionFrontEnd reflectionFrontEnd = new DefaultReflectionFrontEnd();
    public RhinoFrontEnd() {

    }

    public String getClassName() {
        return "RhinoFrontEnd";
    }

    public static void jsFunction_addComponent(Context cx, Scriptable thisObj,Object[] args, Function funObj) throws ClassNotFoundException {
        RhinoFrontEnd rhino = (RhinoFrontEnd) thisObj;
        String componentClass = (String) args[0];
        rhino.reflectionFrontEnd.registerComponentImplementation(componentClass);
    }

    public static Object jsFunction_subContainer(Context cx, Scriptable thisObj,Object[] args, Function funObj) throws NotAFunctionException, PropertyException, JavaScriptException {
        RhinoFrontEnd parent = (RhinoFrontEnd) thisObj;
        RhinoFrontEnd child = new RhinoFrontEnd();
        cx.initStandardObjects(child);
        parent.reflectionFrontEnd.getPicoContainer().addChild(child.reflectionFrontEnd.getPicoContainer());
        //return child; // does not work, but i wish it would
        return thisObj; // works but is wrong return val;
    }
}
