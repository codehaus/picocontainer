/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.nanocontainer.type3msg.sample;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: skizz
 * Date: Sep 10, 2003
 * Time: 10:31:29 PM
 * To change this template use Options | File Templates.
 */
public class PretendDatabase implements Database {
    private List objects;

    public PretendDatabase(List objects) {
        this.objects = objects;
    }

    public void storeObject(String anObject) {
        objects.add(anObject);
    }
}
