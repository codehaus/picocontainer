/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Paul Hammant & Obie Fernandez & Aslak                    *
 *****************************************************************************/

package org.picocontainer.defaults;

import org.picocontainer.ComponentMonitor;

import java.io.Serializable;
import java.lang.reflect.Constructor;

/**
 * @author Paul Hammant & Obie Fernandez
 * @version $Revision$
 */
public class NullComponentMonitor implements ComponentMonitor, Serializable {
    private static NullComponentMonitor instance;

    public void instantiating(Constructor constructor) {
    }

    public void instantiated(Constructor constructor, long beforeTime, long duration) {
    }

    public void instantiationFailed(Constructor constructor, Exception e) {
    }

    public static synchronized NullComponentMonitor getInstance() {
        if (instance == null) {
            instance = new NullComponentMonitor();
        }
        return instance;
    }
}
