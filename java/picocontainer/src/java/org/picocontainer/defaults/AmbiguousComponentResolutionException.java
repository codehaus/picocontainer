/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/
package org.picocontainer.defaults;

import org.picocontainer.PicoInstantiationException;

import java.util.Arrays;

public class AmbiguousComponentResolutionException extends PicoInstantiationException {
    private Class ambiguousClass;
    private final Object[] foundKeys;

    public AmbiguousComponentResolutionException(Class ambiguousClass, Object[] foundKeys) {
        this.ambiguousClass = ambiguousClass;
        this.foundKeys = foundKeys;
    }

    public String getMessage() {
        StringBuffer msg = new StringBuffer();
        msg.append("Ambigious class ");
        msg.append(ambiguousClass);
        msg.append(", ");
        msg.append("resolves to multiple keys ");
        msg.append(Arrays.asList(foundKeys));
        return msg.toString();
    }

    public Object[] getResultingKeys() {
        return foundKeys;
    }
}
