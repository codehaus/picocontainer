package org.nanocontainer.script.groovy;

import groovy.util.BuilderSupport;
import org.picocontainer.MutablePicoContainer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Paul Hammant
 * @version $Revision$
 */
public class SymbolicAOPTestBuilder extends BuilderSupport {

    MutablePicoContainer toOperateOn;

    public SymbolicAOPTestBuilder(MutablePicoContainer toOperateOn) {
        this.toOperateOn = toOperateOn;
    }

    protected void setParent(Object o, Object o1) {
    }

    protected Object createNode(Object name) {
        return createNode(name, Collections.EMPTY_MAP);
    }

    protected Object createNode(Object name, Object value) {
        if (value instanceof Class) {
            Map attributes = new HashMap();
            attributes.put("class", value);
            return createNode(name, attributes);
        }
        return createNode(name);
    }


    protected Object createNode(Object name, Map map) {
        if (name.equals("component")) {
            return toOperateOn.registerComponentImplementation(map.remove("key"), (Class) map.remove("class"));
        } else {
            return null;
        }
    }

    protected Object createNode(Object o, Map map, Object o1) {
        return new Object();
    }
}
