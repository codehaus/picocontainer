/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
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
 * Exception that is thrown as part of the introspection. Raised if a PicoContainer cannot resolve a 
 * type dependency because the registered {@link org.picocontainer.ComponentAdapter}s are not 
 * distinct.  
 * 
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 * @author Jon Tirs&eacute;n
 * @since 1.0
 */
public class AmbiguousComponentResolutionException extends PicoIntrospectionException {
    private Class ambiguousClass;
    private final Object[] ambiguousComponentKeys;

    /**
     * Construct a new exception with the ambigous class type and the ambiguous component keys.
     * 
     * @param ambiguousClass the unresolved dependency type
     * @param componentKeys the ambiguous keys.
     */
    public AmbiguousComponentResolutionException(Class ambiguousClass, Object[] componentKeys) {
        super("");
        this.ambiguousClass = ambiguousClass;
        this.ambiguousComponentKeys = new Class[componentKeys.length];
        for (int i = 0; i < componentKeys.length; i++) {
            ambiguousComponentKeys[i] = componentKeys[i];
        }
    }

    /**
     * @return Returns a string containing the unresolved class type and the ambiguous keys. 
     */
    public String getMessage() {
        StringBuffer msg = new StringBuffer();
        msg.append("Ambiguous ");
        msg.append(ambiguousClass);
        msg.append(", ");
        msg.append("resolves to multiple keys ");
        msg.append(Arrays.asList(getAmbiguousComponentKeys()));
        return msg.toString();
    }

    /**
     * @return Returns the ambiguous component keys as array.
     */
    public Object[] getAmbiguousComponentKeys() {
        return ambiguousComponentKeys;
    }
}
