/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by James Strachan                                           *
 *****************************************************************************/

package org.picocontainer.groovy;

import groovy.util.BuilderSupport;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.codehaus.groovy.runtime.InvokerHelper;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.groovy.PicoBuilderException;

/**
 * Builds trees of Pico containers and Pico components using GroovyMarkup
 * 
 * @author <a href="mailto:james@coredevelopers.net">James Strachan</a>
 * @version $Revision$
 */
public class PicoBuilder extends BuilderSupport {

    public PicoBuilder() {
    }

    protected void setParent(Object parent, Object child) {
        if (parent instanceof MutablePicoContainer) {
            MutablePicoContainer parentContainer = (MutablePicoContainer) parent;
            if (child instanceof MutablePicoContainer) {
                MutablePicoContainer childContainer = (MutablePicoContainer) child;
                parentContainer.addChild(childContainer);
            }
        }
    }

    protected void nodeCompleted(Object parent, Object node) {
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

    protected Object createNode(Object name, Map attributes) {
        if (name.equals("container")) {
            return createContainer(attributes);
        }
        else {
            Object parent = getCurrent();
            if (parent instanceof MutablePicoContainer) {
                MutablePicoContainer pico = (MutablePicoContainer) parent;
                
                if (name.equals("component")) {
                        Class type = (Class) attributes.remove("componentClass");
                        if (type != null) {
                            Object key = attributes.remove("key");
                            if (key != null) {
                                pico.registerComponentImplementation(key, type);
                            }
                            else {
                                pico.registerComponentImplementation(type);
                            }
                        }
                        else {
                            throw new PicoBuilderException("Must specify a componentClass attribute for a component");
                        }
                }
                else if (name.equals("bean")) {
                    // lets create a bean
                    Object answer = createBean(attributes);
                    pico.registerComponentInstance(answer);
                    return answer;
                }
            }
            else {
                throw new PicoBuilderException("method: " + name + " must be a child of a container element");
            }
        }
        throw new PicoBuilderException("uknown method: " + name);
    }

    protected Object createContainer(Map attributes) {
        ComponentAdapterFactory adapterFactory = (ComponentAdapterFactory) attributes.remove("adapterFactory");
        if (adapterFactory != null) {
            return new DefaultPicoContainer(adapterFactory);
        }
        else {
            return new DefaultPicoContainer();
        }
    }
    
    protected Object createBean(Map attributes) {
        Class type = (Class) attributes.remove("beanClass");
        if (type == null) {
            throw new PicoBuilderException("bean must have a beanClass attribute");
        }
        try {
            Object bean = type.newInstance();
            // now lets set the properties on the bean
            for (Iterator iter = attributes.entrySet().iterator(); iter.hasNext(); ) {
                Map.Entry entry = (Map.Entry) iter.next();
                String name = entry.getKey().toString();
                Object value = entry.getValue();
                InvokerHelper.setProperty(bean, name, value);
            }
            return bean;
        }
        catch (Exception e) {
            throw new PicoBuilderException("Failed to create bean of type: " + type + ". Reason: " + e, e);
        }
    }
}
