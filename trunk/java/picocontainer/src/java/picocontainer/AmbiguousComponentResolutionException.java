/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the license.html file.                                                    *
 *                                                                           *
 * Original code by Aslak Hellesoy and Paul Hammant                          *
 *****************************************************************************/
package picocontainer;

public class AmbiguousComponentResolutionException extends PicoStartException {
    private final Class[] ambiguousClasses;

    public AmbiguousComponentResolutionException(Class[] ambiguousClass) {
        this.ambiguousClasses = ambiguousClass;
    }

    public String getMessage() {
        return "Ambiguous Classes: " + ambiguousClasses;
    }

    public Class[] getAmbiguousClasses() {
        return ambiguousClasses;
    }
}
