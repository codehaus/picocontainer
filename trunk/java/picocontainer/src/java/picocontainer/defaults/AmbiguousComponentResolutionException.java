/*****************************************************************************
 * Copyright (Cc) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/
package picocontainer.defaults;

import picocontainer.PicoInstantiationException;

public class AmbiguousComponentResolutionException extends PicoInstantiationException {
    private final Class[] ambiguousClasses;

    public AmbiguousComponentResolutionException(Class[] ambiguousClass) {
        this.ambiguousClasses = ambiguousClass;
    }

    public String getMessage() {
        String msg = "Ambiguous Classes:";
        for (int i = 0; i < ambiguousClasses.length; i++) {
            Class ambiguousClass = ambiguousClasses[i];
            msg = msg + " " + ambiguousClass.getName();
        }
        return msg;
    }

    public Class[] getAmbiguousClasses() {
        return ambiguousClasses;
    }
}
