/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.nanowar.webwork;


import webwork.action.Action;


public class TestAction implements Action {
    String foo;
    public TestAction(String foo) {
        this.foo = foo;
    }
    
    public String getFoo() {
        return foo;
    }
    
    public String execute() {
        return foo;
    }
}
