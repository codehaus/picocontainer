package org.microcontainer.impl;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;

/**
 * Delegation model. i.e. This class implements ClassLoader only and delegates to several classloaders
 * on each invocation of loadClass(). Support late addition of ClassLoaders. Such that .mca files can
 * be deployed late, and still have APIs promoted for common visibility.
 *
 * TODO (from paul) needs a hidden and protected implementation. protected in
 * that the DefaultKernel and package can use it. Protected in that casual navigators of the
 * classloader tree cannot poke it with reflection or cast it to implementation and use it.
 *
 * @author Michael Ward
 * @author Paul Hammant
 * @version $Revision$
 */
public class DelegatingClassLoader extends ClassLoader {

	private List delegates = new LinkedList();

	public DelegatingClassLoader(ClassLoader parent) {
		super(parent);
	}

	/**
	 * Add a class loader to the list of delegates
	 * @param classLoader
	 */
	public void addClassLoader(ClassLoader classLoader) {
		synchronized(delegates) {
			delegates.add(new ClassLoaderWrapper(classLoader));
		}
	}

	/**
	 * Attempts to load class from parent ClassLoader first then tries delegates
	 */
	public Class loadClass(String name) throws ClassNotFoundException {
		Class clazz = null;

		try {
			clazz = super.loadClass(name);
		} catch (ClassNotFoundException e) {
			// ignore
		}

		if(clazz != null) { // found in default class loader
			return clazz;
		}

		return loadClassFromDelegates(name, false);
	}

	public synchronized Class loadClass(String name, boolean resolve) throws ClassNotFoundException {
		Class clazz = null;

		try {
			clazz = super.loadClass(name, resolve);
		} catch (ClassNotFoundException e) {
			// ignore
		}

		if(clazz != null) {
			return clazz;
		}

		return loadClassFromDelegates(name, resolve);
	}

	private Class loadClassFromDelegates(String name, boolean resolve) throws ClassNotFoundException {
		Iterator iter = delegates.iterator();
		Class clazz = null;

		while(iter.hasNext()) {
			ClassLoaderWrapper cl = (ClassLoaderWrapper)iter.next();

			try {
				clazz = cl.loadClass(name, resolve);
			} catch (ClassNotFoundException e) {
				// ignore class not found must iterate through all
			}

			if(clazz != null) {
				return clazz;
			}
		}

		throw new ClassNotFoundException("MicroContainer can not find class: " + name);
	}

	/**
	 * Over ride default to prevent throwing of a ClassNotFoundException we want to iterate the classloaders
	 */
	protected Class findClass(String name) throws ClassNotFoundException {
		return null;
	}

	/**
	 * This class exists just to expose the loadClass(String, boolean) method as public
	 *
	 * TODO (from paul) I think this class needs to copy the functionality of URLClassLoader and somehow
	 * supply classes to the promoted ClassLoader (now DelegatingClassLoader) so that the
	 * theClass.getClass.GetClassLoader() instanceof PromoitedClassLoader. (Not sure if this is possible?)
	 */
	private class ClassLoaderWrapper extends ClassLoader {
		public ClassLoaderWrapper(ClassLoader parent) {
			super(parent);
		}

		public synchronized Class loadClass(String name, boolean resolve) throws ClassNotFoundException {
			return super.loadClass(name, resolve);
		}
	}
}
