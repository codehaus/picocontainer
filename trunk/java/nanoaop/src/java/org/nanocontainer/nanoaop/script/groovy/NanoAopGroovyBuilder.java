/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by various                           *
 *****************************************************************************/
package org.nanocontainer.nanoaop.script.groovy;

import dynaop.Aspects;
import dynaop.Pointcuts;
import dynaop.ProxyFactory;
import org.aopalliance.intercept.MethodInterceptor;
import org.nanocontainer.DefaultNanoContainer;
import org.nanocontainer.NanoContainer;
import org.nanocontainer.aop.AspectablePicoContainer;
import org.nanocontainer.aop.AspectsApplicator;
import org.nanocontainer.aop.AspectsContainer;
import org.nanocontainer.aop.AspectsManager;
import org.nanocontainer.aop.ClassPointcut;
import org.nanocontainer.aop.ComponentPointcut;
import org.nanocontainer.aop.MethodPointcut;
import org.nanocontainer.aop.PointcutsFactory;
import org.nanocontainer.aop.defaults.AspectsComponentAdapterFactory;
import org.nanocontainer.aop.dynaop.DynaopAspectsManager;
import org.nanocontainer.aop.dynaop.DynaopPointcutsFactory;
import org.nanocontainer.aop.dynaop.InstanceMixinFactory;
import org.nanocontainer.script.groovy.NanoContainerBuilder;
import org.nanocontainer.script.groovy.NanoContainerBuilderException;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.ComponentAdapterFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Extends <code>org.nanocontainer.script.groovy.NanoContainerBuilder</code> to
 * provide support for adding aspects.
 * 
 * @author Stephen Molitor
 * @version $Revision$
 * @see org.nanocontainer.script.groovy.NanoContainerBuilder
 */
public class NanoAopGroovyBuilder extends NanoContainerBuilder {



    protected Object createNode(Object name, Map attributes) {
        if (name.equals("component")) {
        }
        return super.createNode(name, attributes);
    }

    protected NanoContainer createChildContainer(Map attributes, NanoContainer parent) {
        NanoContainer nano = super.createChildContainer(newAttributes, parent);
    }





}