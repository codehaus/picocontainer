/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/

package org.nanocontainer;

import org.picocontainer.PicoCompositionException;
import org.xml.sax.SAXException;

public class SAXCompositionException extends PicoCompositionException{
    private final SAXException se;

    public SAXCompositionException(SAXException se) {
        this.se = se;
    }

    public SAXException getSe() {
        return se;
    }
}
