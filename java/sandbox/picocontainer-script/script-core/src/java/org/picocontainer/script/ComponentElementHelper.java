/*******************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved. 
 * ---------------------------------------------------------------------------
 * The software in this package is published under the terms of the BSD style
 * license a copy of which has been included with this distribution in the
 * LICENSE.txt file. 
 ******************************************************************************/
package org.picocontainer.script;

import org.picocontainer.Parameter;

import java.util.Properties;


public class ComponentElementHelper {

    public static Object makeComponent(Object classNamekey, Object key, Parameter[] parameters, Object componentClass, ScriptedPicoContainer current, Object instance, Properties[] properties) {
        ScriptedPicoContainer container = current;
        if (properties.length != 0) {
            container = (ScriptedPicoContainer) current.as(properties);
        }
        if (classNamekey != null)  {
            key = new ClassName((String)classNamekey);
        }

        if (componentClass instanceof Class) {
            Class clazz = (Class) componentClass;
            key = key == null ? clazz : key;
            return container.addComponent(key, clazz, parameters);
        } else if (componentClass instanceof String) {
            String className = (String) componentClass;
            key = key == null ? className : key;
            return container.addComponent(key, new ClassName(className), parameters);
        } else if (instance != null) {
            key = key == null ? instance.getClass() : key;
            return container.addComponent(key, instance);
        } else {
            throw new ScriptedPicoContainerMarkupException("Must specify a 'class' attribute for a component as a class name (string) or Class.");
        }
    }

    public static Object makeComponent(Object cnkey,
                                       Object key,
                                       Parameter[] parameters,
                                       Object classValue,
                                       ScriptedPicoContainer container, Object instance) {
        return makeComponent(cnkey, key, parameters, classValue, container, instance, new Properties[0]);
    }
}
