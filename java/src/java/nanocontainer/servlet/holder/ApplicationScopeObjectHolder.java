/*****************************************************************************
 * Copyright (Cc) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Joe Walnes                                               *
 *****************************************************************************/


package nanocontainer.servlet.holder;

import nanocontainer.servlet.ObjectHolder;
import javax.servlet.ServletContext;


/**
 * Holds an object in the ServletContext (application scope)
 */

public class ApplicationScopeObjectHolder implements ObjectHolder {

    private ServletContext context;
    private String key;

    public ApplicationScopeObjectHolder(ServletContext context, String key) {
        this.context = context;
        this.key = key;
    }

    public void put(Object item) {
        context.setAttribute(key, item);
    }

    public Object get() {
        return context.getAttribute(key);
    }

}

