/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by various                           *
 *****************************************************************************/
package org.nanocontainer.script.groovy;

/**
 * @author Stephen Molitor
 */
public class HasParams {

    private String params;

    public HasParams(String a, String b, String c) {
        params = a + b + c;
    }

    public String getParams() {
        return params;
    }
}
