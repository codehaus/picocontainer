/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Joe Walnes                                               *
 *****************************************************************************/


package nanocontainer.servlet.holder;

import nanocontainer.servlet.ObjectHolder;

import javax.servlet.http.HttpSession;

/**
 * Holds an object in the HttpSession
 */

public class SessionScopeObjectHolder implements ObjectHolder {

    private HttpSession session;
    private String key;

    public SessionScopeObjectHolder(HttpSession session, String key) {
        this.session = session;
        this.key = key;
    }

    public void put(Object item) {
        session.setAttribute(key, item);
    }

    public Object get() {
        return session.getAttribute(key);
    }

}

