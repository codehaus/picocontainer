/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.nanocontainer.servlet;

import junit.framework.TestCase;
import org.jmock.C;
import org.jmock.Mock;
import org.picocontainer.defaults.ObjectReference;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class ReferenceTestCase extends TestCase {
    private final String key = "foo";
    private final Object value = new Object();

    public void testRequestScope() throws UnsupportedEncodingException {
        Mock mock = createMock(ServletRequest.class);
        RequestScopeObjectReference ref = new RequestScopeObjectReference((ServletRequest) mock.proxy(), key);
        setGetAndVerify(ref, mock);
    }

    public void testApplicationScope() {
        Mock mock = createMock(ServletContext.class);
        ApplicationScopeObjectReference ref = new ApplicationScopeObjectReference((ServletContext) mock.proxy(), key);
        setGetAndVerify(ref, mock);
    }

    public void testSessionScope() {
        Mock mock = createMock(HttpSession.class);
        SessionScopeObjectReference ref = new SessionScopeObjectReference((HttpSession) mock.proxy(), key);
        setGetAndVerify(ref, mock);
    }

    private void setGetAndVerify(ObjectReference ref, Mock mock) {
        ref.set(value);
        assertEquals(value, ref.get());
        mock.verify();
    }

    private Mock createMock(final Class clazz) {
        Mock mock = new Mock(clazz);
        mock.expect("setAttribute", C.eq(key, value));
        mock.expectAndReturn("getAttribute", C.args(C.eq(key)), value);
        return mock;
    }

}
