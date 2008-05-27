/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package org.nanocontainer.testmodel;

public class WilmaImpl implements Wilma {

    private boolean helloCalled;

    public boolean helloCalled() {
        return helloCalled;
    }

    public void hello() {
        helloCalled = true;
    }
}
