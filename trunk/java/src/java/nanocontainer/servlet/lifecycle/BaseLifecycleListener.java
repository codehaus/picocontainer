/*****************************************************************************
 * Copyright (Cc) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Joe Walnes                                               *
 *****************************************************************************/


package nanocontainer.servlet.lifecycle;


import picocontainer.PicoContainer;


import javax.servlet.ServletContext;


import nanocontainer.servlet.holder.ApplicationScopeObjectHolder;

import nanocontainer.servlet.containerfactory.XmlConfiguredNanoFactory;

import nanocontainer.servlet.ContainerFactory;

import nanocontainer.servlet.ObjectHolder;


public class BaseLifecycleListener {


    public static final String FACTORY_KEY = "nanocontainer.containerfactory";

    public static final String CONTAINER_KEY = "nanocontainer.container";

    public static final String INSTANTIATER_KEY = "nanocontainer.instantiater";


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

