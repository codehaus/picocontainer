/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by James Strachan                                           *
 *****************************************************************************/

package org.nanocontainer.script.groovy.buildernodes;

import java.util.Map;

import org.nanocontainer.NanoContainer;
import org.picocontainer.MutablePicoContainer;
import org.nanocontainer.script.NanoContainerMarkupException;
import java.util.Iterator;
import org.codehaus.groovy.runtime.InvokerHelper;

/**
 * Creates on-the-spot Javabeans configurations and registers the result with
 * the container via pico.registerCompoenntInstance.
 * @author James Strachan
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 * @author Michael Rimov
 * @author Mauro Talevi
 * @version $Revision: 2695 $
 */
public class BeanNode extends AbstractCustomBuilderNode {

    /**
     * The name of the node we're handling.
     */
    public static final String NODE_NAME = "bean";

    /**
     * Bean class attribute.
     */
    public static final String BEAN_CLASS = "beanClass";


    /**
     * Default constructor.
     */
    public BeanNode() {
        super(NODE_NAME);
    }

    public Object createNewNode(NanoContainer parentContainer, Map attributes) throws ClassNotFoundException {
        MutablePicoContainer pico = parentContainer.getPico();
        Object bean = createBean(attributes);
        pico.registerComponentInstance(bean);
        return bean;
    }


    /**
     * Instantiates the bean and sets the appropriate attributes.  It then
     * @param attributes Map
     * @return Object resulting JavaBean.
     */
    protected Object createBean(final Map attributes) {
        Class type = (Class) attributes.remove(BEAN_CLASS);
        if (type == null) {
            throw new NanoContainerMarkupException("Bean must have a beanClass attribute");
        }
        try {
            Object bean = type.newInstance();
            // now let's set the properties on the bean
            for (Iterator iter = attributes.entrySet().iterator(); iter.hasNext();) {
                Map.Entry entry = (Map.Entry) iter.next();
                String name = entry.getKey().toString();
                Object value = entry.getValue();
                InvokerHelper.setProperty(bean, name, value);
            }
            return bean;
        } catch (IllegalAccessException e) {
            throw new NanoContainerMarkupException("Failed to create bean of type '" + type + "'. Reason: " + e, e);
        } catch (InstantiationException e) {
            throw new NanoContainerMarkupException("Failed to create bean of type " + type + "'. Reason: " + e, e);
        }
    }

    /**
     * {@inheritDoc}
     * <p>This version only checks for 'beanClass' and lets all other attributes
     * through (since they become property values)</p>
     * @param specifiedAttributes Map
     * @throws NanoContainerMarkupException
     */
    public void validateScriptedAttributes(Map specifiedAttributes) throws NanoContainerMarkupException {
        if (!specifiedAttributes.containsKey(BEAN_CLASS)) {
            throw new NanoContainerMarkupException("Attribute " + BEAN_CLASS + " is required.");
        }

        //Assume all other attributes
    }
}
