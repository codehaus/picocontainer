/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Joerg Schaible                                           *
 *****************************************************************************/
package org.nanocontainer.remoting.ejb.testmodel;



/** EJB Home implementation */
public class HelloHomeImpl extends EJBHomeMock implements HelloHome {
    /** @return Returns the EJB */
    public Hello create() {
        return new HelloImpl();
    }
}