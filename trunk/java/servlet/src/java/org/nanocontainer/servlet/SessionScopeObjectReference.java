/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.servlet;

import org.picocontainer.defaults.ObjectReference;

import javax.servlet.http.HttpSession;

/**
 * References an object that lives as an attribute of the
 * HttpSession.
 *
 * @author <a href="mailto:joe@thoughtworks.net">Joe Walnes</a>
 */
public class SessionScopeObjectReference implements ObjectReference {

    private HttpSession session;
    private String key;

    public SessionScopeObjectReference(HttpSession session, String key) {
        this.session = session;
        this.key = key;
    }

    public void set(Object item) {
        session.setAttribute(key, item);
    }

    public Object get() {
        return session.getAttribute(key);
    }

}
