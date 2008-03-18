/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Joerg Schaible                                           *
 *****************************************************************************/
package org.picocontainer.testmodel;

public class PurseBean {
    PersonBean owner;
    public PersonBean getOwner() {
        return owner;
    }
    public void setOwner(PersonBean owner) {
        this.owner = owner;
    }
}
