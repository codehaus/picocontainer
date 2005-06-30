/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.nanowar;

import javax.servlet.ServletRequest;

import org.picocontainer.defaults.ObjectReference;

/**
 * References an object that lives as an attribute of the ServletRequest.
 * 
 * @author <a href="mailto:joe@thoughtworks.net">Joe Walnes</a>
 */
public class RequestScopeObjectReference implements ObjectReference {

	private ServletRequest request;

	private String key;

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
