/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Joerg Schaible                                           *
 *****************************************************************************/
package org.nanocontainer.ejb.testmodel;

import javax.ejb.EJBObject;


/** Simple EJB interface */
public interface Hello extends EJBObject {
    /** @return Returns &quot;Hello World&quot; */
    public String getHelloWorld();
}