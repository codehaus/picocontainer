/*****************************************************************************
 * Copyright (C) OldPicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Jon Tirsen                        *
 *****************************************************************************/

package org.picocontainer;

import org.picocontainer.defaults.AmbiguousComponentResolutionException;
import org.picocontainer.defaults.ComponentAdapter;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.NotConcreteRegistrationException;

/**
 * @author Jon Tirsen (tirsen@codehaus.org)
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public interface Parameter {
    ComponentAdapter resolveAdapter(MutablePicoContainer componentRegistry) throws PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException;
}
