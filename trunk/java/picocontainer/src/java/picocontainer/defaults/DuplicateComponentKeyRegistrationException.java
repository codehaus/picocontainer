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

import picocontainer.PicoRegistrationException;

public class DuplicateComponentKeyRegistrationException extends PicoRegistrationException {
    private Object key;

    public DuplicateComponentKeyRegistrationException(Object key) {
        this.key = key;
    }

    public Object getDuplicateKey() {
        return key;
    }

    public String getMessage() {
        return "Key " + key + " duplicated";
    }
}
