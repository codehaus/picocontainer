/*****************************************************************************
 * Copyright (C) ClassRegistrationPicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package picocontainer.hierarchical;

import picocontainer.PicoRegistrationException;

/**
 *
 * @author Aslak Hellesoy
 * @version $Revision: 1.6 $
 */
public class NotConcreteRegistrationException extends PicoRegistrationException {
    private final Class componentImplementation;

    public NotConcreteRegistrationException(Class componentImplementation) {
        this.componentImplementation = componentImplementation;
    }

    public String getMessage() {
        return "Bad Access: '" + componentImplementation.getName() + "' is not instansiable";
    }

    public Class getComponentImplementation() {
        return componentImplementation;
    }
}
