/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Jon Tirsen                        *
 *****************************************************************************/

package org.picocontainer.internals;

import org.picocontainer.PicoInstantiationException;

/**
 * @author Jon Tirsen (tirsen@codehaus.org)
 * @version $Revision$
 */
public class ConstantParameter implements Parameter {
    private Object arg;

    public ConstantParameter(Object parameter) {
        this.arg = parameter;
    }

    public Object resolve(ComponentRegistry componentRegistry, ComponentAdapter compSpec, Class targetType) throws PicoInstantiationException {
        return arg;
    }
}
