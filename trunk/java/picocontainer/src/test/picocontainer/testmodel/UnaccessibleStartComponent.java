/*****************************************************************************
 * Copyright (C) ClassRegistrationPicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package picocontainer.testmodel;

import java.util.List;

/**
 *
 * @author Aslak Hellesoy
 * @version $Revision: 0 $
 */
public class UnaccessibleStartComponent extends Object {
    private List messages;

    public UnaccessibleStartComponent(List messages) {
        this.messages = messages;
    }

    private final void start() {
        messages.add("started");
    }
}
