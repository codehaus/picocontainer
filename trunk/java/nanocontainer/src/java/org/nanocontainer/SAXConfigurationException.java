/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/

package org.nanocontainer;

import org.picocontainer.PicoConfigurationException;
import org.xml.sax.SAXException;

public class SAXConfigurationException extends PicoConfigurationException{
    private final SAXException se;

    public SAXConfigurationException(SAXException se) {
        this.se = se;
    }

    public SAXException getSe() {
        return se;
    }
}
