package org.microcontainer.impl;

import java.net.URLClassLoader;
import java.net.URL;

/**
 * @author Paul Hammant
 * @version $Revision$
 */
public class StandardMicroClassLoader extends URLClassLoader {
    public StandardMicroClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

}
