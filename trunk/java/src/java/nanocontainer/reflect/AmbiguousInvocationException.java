/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the license.html file.                                                    *
 *                                                                           *
 * Original code by Aslak Hellesoy                                           *
 *****************************************************************************/

package nanocontainer.reflect;

import java.lang.reflect.Method;

/**
 *
 * @author Aslak Hellesoy
 * @version $Revision$
 */
public class AmbiguousInvocationException extends Exception {
    private final Object firstComponent;
    private final Object secondComponent;
    private final Method method;

    public AmbiguousInvocationException(Object firstComponent, Object secondComponent, Method method) {
        this.firstComponent = firstComponent ;
        this.secondComponent = secondComponent;
        this.method = method;
    }

    public Object getFirstComponent() {
        return firstComponent;
    }

    public Object getSecondComponent() {
        return secondComponent;
    }

    public String getMessage() {
        return "Ambiguous invocation of" + method + ": " + firstComponent.getClass().getName() + ", " + secondComponent.getClass().getName();
    }
}
