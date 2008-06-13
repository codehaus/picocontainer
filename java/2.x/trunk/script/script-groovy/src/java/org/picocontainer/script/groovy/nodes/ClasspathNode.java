/*******************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.
 * ---------------------------------------------------------------------------
 * The software in this package is published under the terms of the BSD style
 * license a copy of which has been included with this distribution in the
 * LICENSE.txt file. 
 ******************************************************************************/
package org.picocontainer.script.groovy.nodes;

import java.util.Map;

import org.picocontainer.classname.ClassPathElement;
import org.picocontainer.classname.ClassLoadingPicoContainer;
import org.picocontainer.script.util.ClassPathElementHelper;


/**
 * @author James Strachan
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 * @author Michael Rimov
 * @author Mauro Talevi
 */
public class ClasspathNode extends AbstractBuilderNode {

    public static final String NODE_NAME = "classPathElement";


    private static final String PATH = "path";


    public ClasspathNode() {
        super(NODE_NAME);

        addAttribute(PATH);
    }


    public Object createNewNode(Object current, Map attributes) {
        return createClassPathElementNode(attributes, (ClassLoadingPicoContainer) current);
    }

    private ClassPathElement createClassPathElementNode(Map attributes, ClassLoadingPicoContainer container) {

        final String path = (String) attributes.remove(PATH);
        return ClassPathElementHelper.addClassPathElement(path, container);
    }

}
