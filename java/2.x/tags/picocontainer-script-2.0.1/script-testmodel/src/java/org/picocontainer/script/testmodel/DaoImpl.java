/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by various                           *
 *****************************************************************************/
package org.picocontainer.script.testmodel;



/**
 * @author Stephen Molitor
 */
public class DaoImpl implements Dao {

    public DaoImpl() {
        System.err.println("");
    }

    public String loadData() {
        return "data";
    }

}