/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/

package org.nanocontainer.script.xml;

public class TestBean {
    private int foo;
    private String bar;
    private String constructorCalled;
    
    public TestBean() {
        constructorCalled="default";
    }
    
    public TestBean(String greedy) {
         constructorCalled="greedy";
    }
    
    public String getConstructorCalled() {
        return constructorCalled;
    }
    public int getFoo() {
        return foo;
    }

    public String getBar() {
        return bar;
    }

    public void setFoo(int foo) {
        this.foo = foo;
    }

    public void setBar(String bar) {
        this.bar = bar;
    }
}
