/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Aslak Hellesoy and Paul Hammant                          *
 *****************************************************************************/

package org.nanocontainer.script.xml;

import org.w3c.dom.Element;

/**
 * Factory that creates instances from DOM Elements
 * @author Paul Hammant
 * @author Marcos Tarruella
 */
public interface XMLComponentInstanceFactory {
    /**
     * Creates an instance of an Object from a DOM Element
     * @param element the DOM Element
     * @return An Object instance 
     * @throws ClassNotFoundException
     */
    Object makeInstance(Element element) throws ClassNotFoundException;
}
