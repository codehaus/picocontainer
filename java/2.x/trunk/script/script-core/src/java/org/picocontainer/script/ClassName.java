/*******************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved. 
 * ---------------------------------------------------------------------------
 * The software in this package is published under the terms of the BSD style
 * license a copy of which has been included with this distribution in the
 * LICENSE.txt file. 
 ******************************************************************************/
package org.picocontainer.script;

/**
 * ClassName is a simple wrapper for a class name which is used as a key in
 * the registration of components in PicoContainer.
 * 
 * @author Paul Hammant
 */
public class ClassName implements CharSequence {
    final String className;

    public ClassName(String className) {
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

    public int length() {
        return className.length();
    }

    public char charAt(int ix) {
        return className.charAt(ix);
    }

    public CharSequence subSequence(int from, int to) {
        return className.subSequence(from, to);
    }

    public String toString() {
        return className.toString();
    }

    public int hashCode() {
        return className.hashCode();
    }

    public boolean equals(Object o) {
        return className.equals(o);
    }
}
