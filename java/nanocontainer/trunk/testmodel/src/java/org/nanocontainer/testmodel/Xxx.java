/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.testmodel;

import junit.framework.Assert;
import org.picocontainer.Disposable;
import org.picocontainer.Startable;

/**
 * An abstract component and three dependancies used for testing.
 */
public abstract class Xxx implements Startable, Disposable {

    public static String componentRecorder = "";

    public static void reset() {
        componentRecorder = "";
    }

    public void start() {
        componentRecorder += "<" + code();
    }

    public void stop() {
        componentRecorder += code() + ">";
    }

    public void dispose() {
        componentRecorder += "!" + code();
    }

    private String code() {
        String name = getClass().getName();
        return name.substring(name.indexOf('$') + 1, name.length());
    }

    public static class A extends Xxx {
    }

    public static class B extends Xxx {
        A a;

        public B(A a) {
            Assert.assertNotNull(a);
            this.a = a;
        }

        public A getA() {
            return a;
        }
    }

    public static class C extends Xxx {
    }
}
