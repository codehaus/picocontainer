/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package org.picocontainer.testmodel;

import junit.framework.Assert;

import java.io.Externalizable;
import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;

public class FredImpl implements Externalizable {
    private Wilma wilma;

    // to satify Externalizable
    public FredImpl() {
    }

    public FredImpl(Wilma wilma) {
        Assert.assertNotNull("Wilma cannot be passed in as null", wilma);
        wilma.hello();
        this.wilma = wilma;
    }

    public Wilma getWilma() {
        return wilma;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        // whatever
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        // whatever
    }


}
