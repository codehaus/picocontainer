/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/

package org.nanocontainer.xml;

public class EmptyXmlConfigurationException extends XmlFrontEndException {
    public EmptyXmlConfigurationException() {
    }

    public String getMessage() {
        return "No components in the XML configuration";
    }
}
