package org.microcontainer.impl;

import java.net.URLClassLoader;
import java.net.URL;

/**
 * TODO This should actually be a delegation model. i.e. This class implements ClassLoader only
 * and delegates to several URL classloaders on each invocation of loadClass().
 * It also has a hidden and protected implementation. protected in that the DefaultKernel and package can use it.
 * Protected in that casual navigators of the classloader tree cannot poke it with reflection or cast it to
 * implementation and use it.
 * @author Paul Hammant
 * @version $Revision$
 */


public class PromotedClassLoader extends ClassLoader {

    private transient HiddenPromotedClassLoader hiddenPromotedClassLoader;

    public PromotedClassLoader(HiddenPromotedClassLoader hiddenPromotedClassLoader, ClassLoader parent) {
        super(parent);
        this.hiddenPromotedClassLoader = hiddenPromotedClassLoader;
    }

    public Class loadClass(String name) throws ClassNotFoundException {
        Class clazz = hiddenPromotedClassLoader.loadClass(name);
        if (clazz == null) {
            return super.loadClass(name);
        }
        return clazz;
    }

    protected synchronized Class loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class clazz = hiddenPromotedClassLoader.loadClass(name, resolve);
        if (clazz == null) {
            return super.loadClass(name, resolve);
        }
        return clazz;
    }

}
