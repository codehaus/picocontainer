package org.microcontainer.impl;

import java.net.URLClassLoader;
import java.net.URL;

/**
 * TODO I think this class needs to copy the functionality of URLClassLoader and somehow supply classes
 * to PromotedClassLoader so that the theClass.getClass.GetClassLoader() instanceof PromoitedClassLoader.
 * It should also support late addition of URLs. Such that .mca files can be deployed late, and still
 * have APIs promoted for common visibility.
 * @author Paul Hammant
 * @version $Revision$
 */
public class HiddenPromotedClassLoader extends URLClassLoader {
    public HiddenPromotedClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public synchronized Class loadClass(String name, boolean resolve) throws ClassNotFoundException {
        return super.loadClass(name, resolve);
    }
}
