/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
package foo.bar;

import org.picocontainer.Startable;

public class Zap implements Startable {
    private final String hello;
    private String toString = "Not started";

    public Zap(String hello) {
        this.hello = hello;
    }

    public void start() {
        toString = hello + " Started";
    }

    public void stop() {

    }

    public String toString() {
        return toString;
    }
}