/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by James Strachan                                           *
 *****************************************************************************/

package org.nanocontainer.script.groovy.buildernodes;

import java.util.Map;
import java.security.Permission;

import org.nanocontainer.NanoContainer;
import org.nanocontainer.DefaultNanoContainer;
import org.nanocontainer.ClassPathElement;
import org.nanocontainer.script.NanoContainerMarkupException;

/**
 * @author Paul Hammant
 * @version $Revision: 2695 $
 */
public class GrantElementNode extends AbstractCustomBuilderNode {

    public static final String NODE_NAME = "grant";

    public GrantElementNode() {
        super(NODE_NAME);
    }



    public Object createNewNode(Object current, Map attributes) {

        if (!(current instanceof ClassPathElement)) {
            throw new NanoContainerMarkupException("Don't know how to create a 'grant' child of a '" + current.getClass() + "'parent");
        }

        Permission perm = (Permission) attributes.remove("class");
        ClassPathElement cpe = (ClassPathElement) current;

        return cpe.grantPermission(perm);
    }

}
