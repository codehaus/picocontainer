/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Joe Walnes                                               *
 *****************************************************************************/


package org.nanocontainer.servlet.lifecycle;


import org.picocontainer.PicoContainer;


import javax.servlet.ServletContext;


import org.nanocontainer.servlet.holder.ApplicationScopeObjectHolder;

import org.nanocontainer.servlet.containerfactory.XmlConfiguredNanoFactory;

import org.nanocontainer.servlet.ContainerFactory;

import org.nanocontainer.servlet.ObjectHolder;


public class BaseLifecycleListener {


    public static final String FACTORY_KEY = "org.nanocontainer.containerfactory";

    public static final String CONTAINER_KEY = "org.nanocontainer.container";

    public static final String INSTANTIATER_KEY = "org.nanocontainer.instantiater";


    protected ContainerFactory getFactory(ServletContext context) {

        ObjectHolder holder = new ApplicationScopeObjectHolder(context, FACTORY_KEY);

        ContainerFactory result = (ContainerFactory) holder.get();

        if (result == null) {

            result = new XmlConfiguredNanoFactory(context);

            holder.put(result);

        }

        return result;

    }


    protected void destroyContainer(ServletContext context, ObjectHolder holder) {

        PicoContainer container = (PicoContainer) holder.get();

        getFactory(context).destroyContainer(container);

    }


}

