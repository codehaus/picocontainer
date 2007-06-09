/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.nanowar;

import org.picocontainer.defaults.ObjectReference;

import javax.servlet.ServletRequest;

/**
 * References an object that lives as an attribute of the
 * ServletRequest.
 *
 * @author <a href="mailto:joe@thoughtworks.net">Joe Walnes</a>
 */
public final class RequestScopeObjectReference implements ObjectReference {

    private final ServletRequest request;
    private final String key;

    public RequestScopeObjectReference(ServletRequest request, String key) {
        this.request = request;
        this.key = key;
    }

    public void set(Object item) {
        request.setAttribute(key, item);
    }

    public Object get() {
        return request.getAttribute(key);
    }

}
