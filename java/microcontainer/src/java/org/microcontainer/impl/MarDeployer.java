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

import java.net.JarURLConnection;
import java.net.URL;
import java.util.jar.JarFile;
import java.util.jar.JarEntry;
import java.util.Enumeration;
import java.io.*;

/**
 * @author Mike Ward
 * Responsible for deploying a MAR to the file system.
 */
public class MarDeployer {
	protected File workingDir = null;

	public MarDeployer() {
		workingDir = new File("work"); // todo this should be configurable! Pico-tize?
		workingDir.mkdir();
	}

	public File getWorkingDir() {
		return workingDir;
	}

	public void deploy(String context, URL mar) throws IOException {
		File sandboxDir = new File(workingDir, context);
		sandboxDir.mkdir();

		// Expand the MAR into working directory
		JarURLConnection juc = (JarURLConnection) mar.openConnection();
		juc.setUseCaches(false);
		JarFile jarFile = null;
		InputStream input = null;

		try {
			jarFile = juc.getJarFile();
			Enumeration jarEntries = jarFile.entries();

			while (jarEntries.hasMoreElements()) {
				JarEntry jarEntry = (JarEntry) jarEntries.nextElement();
				String name = jarEntry.getName();

				int last = name.lastIndexOf('/');
				if (last >= 0) {
					new File(sandboxDir, name.substring(0, last))
							.mkdirs();
				}
				if (name.endsWith("/")) {
					continue;
				}
				input = jarFile.getInputStream(jarEntry);
				expand(input, sandboxDir, name);
				input.close();
				input = null;
			}
		} catch (IOException e) {
			// problem, cleanup directory
			deleteDir(sandboxDir);
			throw e;
		} finally {
			if (input != null) {
				input.close();
				input = null;
			}
			if (jarFile != null) {
				jarFile.close();
				jarFile = null;
			}
		}
	}

	protected void deleteDir(File dir) {
		String files[] = dir.list();
        if (files == null) {
            files = new String[0];
        }
        for (int i = 0; i < files.length; i++) {
            File file = new File(dir, files[i]);
            if (file.isDirectory()) {
                deleteDir(file);
            } else {
                file.delete();
            }
        }
        dir.delete();
	}

	protected void expand(InputStream input, File docBase, String name) throws IOException {
		File file = new File(docBase, name);
		BufferedOutputStream bos = null;

		try {
			bos = new BufferedOutputStream(new FileOutputStream(file));
			byte buffer[] = new byte[2048];
			while (true) {
				int n = input.read(buffer);
				if (n <= 0) {
					break;
				}
				bos.write(buffer, 0, n);
			}
		} finally {
			bos.close();
		}
	}
}
