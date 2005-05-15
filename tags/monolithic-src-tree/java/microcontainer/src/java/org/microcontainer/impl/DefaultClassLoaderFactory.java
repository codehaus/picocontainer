/*****************************************************************************
 * Copyright (C) MicroContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Mike Ward                                                *
 *****************************************************************************/

package org.microcontainer.impl;

import org.microcontainer.ClassLoaderFactory;

import java.io.File;
import java.net.URL;
import java.net.MalformedURLException;
import java.net.URLClassLoader;

/**
 * @author Michael Ward
 * @version $Revision$
 */
public class DefaultClassLoaderFactory implements ClassLoaderFactory {
	public static final String PROMOTED_PATH = "/MCA-INF/promoted/";
	public static final String COMPONENT_PATH = "/MCA-INF/components/";
	public static final String LIB_PATH = "/MCA-INF/lib/";
	public static final String DEFAULT_WORK_DIR = "work.dir";
	private Configuration configuration;
	private DelegatingClassLoader delegatingClassLoader; // promoted classloaders

	public DefaultClassLoaderFactory(Configuration configuration) {
		this.configuration = configuration;
		this.delegatingClassLoader = new DelegatingClassLoader(this.getClass().getClassLoader());
	}

	public ClassLoader build(String contextName) {
		URL[] urls = getURLs(contextName, PROMOTED_PATH);
		ClassLoader hidden = new URLClassLoader(urls, this.getClass().getClassLoader());
        delegatingClassLoader.addClassLoader(hidden);

		return new StandardMicroClassLoader(getStandardApiURLs(contextName), delegatingClassLoader);
	}

	protected URL[] getStandardApiURLs(String context) {
		URL[] componentURLs = getURLs(context, COMPONENT_PATH);
		URL[] libURLs = getURLs(context, LIB_PATH);
		int total = componentURLs.length + libURLs.length;
		URL[] urls = new URL[total];

		// build for lib directory
		for(int i = 0; i < libURLs.length; i++) {
			urls[i] = libURLs[i];
		}

		// build for components directory
		for(int i = 0; i < componentURLs.length; i++) {
			urls[i + libURLs.length] = componentURLs[i];
		}

		return urls;
	}

	protected URL[] getURLs(String context, String path) {
		File dir = new File(configuration.getWorkDir(), context + path);
		File[] files = dir.listFiles();

		if(files == null) {
			return new URL[0];
		}

		URL[] urls = new URL[files.length];

		try {
			for(int i = 0; i < files.length; i++) {
				urls[i] = files[i].toURL();
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return urls;
	}

}


