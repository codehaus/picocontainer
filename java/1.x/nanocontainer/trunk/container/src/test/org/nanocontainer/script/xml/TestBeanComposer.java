/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/

package org.nanocontainer.script.xml;

public class TestBeanComposer {

    private TestBean bean1;
    private TestBean bean2;

    public TestBeanComposer(TestBean bean1, TestBean bean2) {
        this.bean1 = bean1;
        this.bean2 = bean2;
    }

    public TestBean getBean1() {
        return bean1;
    }

    public TestBean getBean2() {
        return bean2;
    }
}
