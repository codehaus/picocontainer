/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/
package picocontainer.defaults;

import picocontainer.PicoInstantiationException;

import java.util.Arrays;

public class AmbiguousComponentResolutionException extends PicoInstantiationException {
    private Class ambiguousClass;
    private final Class[] resultingClasses;

    public AmbiguousComponentResolutionException(Class ambiguousClass, Class[] resultingClasses) {
        this.ambiguousClass = ambiguousClass;
        this.resultingClasses = resultingClasses;
    }

    public String getMessage() {
        StringBuffer msg = new StringBuffer();
        msg.append("Ambigious class ");
        msg.append(ambiguousClass);
        msg.append(", ");
        msg.append("resolves to ");
        msg.append(Arrays.asList(resultingClasses));
        return msg.toString();
    }

    public Class[] getResultingClasses() {
        return resultingClasses;
    }
}
