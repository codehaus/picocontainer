/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Joe Walnes                                               *
 *****************************************************************************/


package org.nanocontainer.servlet.holder;

import org.nanocontainer.servlet.ObjectHolder;

import javax.servlet.http.HttpServletRequest;


/**
 * Holds an object in the ServletRequest
 */

public class RequestScopeObjectHolder implements ObjectHolder {

    private HttpServletRequest request;
    private String key;

    public RequestScopeObjectHolder(HttpServletRequest request, String key) {
        this.request = request;
        this.key = key;
    }

    public void put(Object item) {
        request.setAttribute(key, item);
    }

    public Object get() {
        return request.getAttribute(key);
    }

}

