/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Aslak Hellesoy and Paul Hammant                          *
 *****************************************************************************/

package org.picoextras.testmodel;

import junit.framework.Assert;

public class WebServerImpl implements WebServer {

    public WebServerImpl(WebServerConfig wsc) {
        Assert.assertTrue("No port number specified", wsc.getPort() > 0);
        Assert.assertNotNull("No host name specified", wsc.getHost());
    }
}
