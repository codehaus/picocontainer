/*****************************************************************************
 * Copyright (ComponentC) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/
package org.picocontainer.defaults;

import org.picocontainer.PicoIntrospectionException;

import java.util.Arrays;

/**
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 * @author Jon Tirs&eacute;n
 */
public class AmbiguousComponentResolutionException extends PicoIntrospectionException {
    private Class ambiguousClass;
    private final Object[] ambiguousComponentKeys;

    public AmbiguousComponentResolutionException(Class ambiguousClass, Object[] componentKeys) {
        this.ambiguousClass = ambiguousClass;
        this.ambiguousComponentKeys = new Class[componentKeys.length];
        for (int i = 0; i < componentKeys.length; i++) {
            ambiguousComponentKeys[i] = componentKeys[i];
        }
    }

    public String getMessage() {
        StringBuffer msg = new StringBuffer();
        msg.append("Ambiguous class ");
        msg.append(ambiguousClass);
        msg.append(", ");
        msg.append("resolves to multiple keys ");
        msg.append(Arrays.asList(getAmbiguousComponentKeys()));
        return msg.toString();
    }

    public Object[] getAmbiguousComponentKeys() {
        return ambiguousComponentKeys;
    }
}
