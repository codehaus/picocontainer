/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Jon Tirsen                        *
 *****************************************************************************/

package org.picocontainer.defaults;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.ComponentAdapter;

/**
 * A ConstantParameter should be used to pass in "constant" arguments
 * to constructors. This includes {@link String}s, {@link Integer}s or
 * any other object that is not registered in the container.
 *
 * @author Jon Tirs&eacute;n
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class ConstantParameter implements Parameter {
    private final Object value;

    public ConstantParameter(Object value) {
        this.value = value;
    }

    public ComponentAdapter resolveAdapter(MutablePicoContainer picoContainer) throws AssignabilityRegistrationException, NotConcreteRegistrationException {
        return new InstanceComponentAdapter(value, value);
    }
}
