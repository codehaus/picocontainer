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

import org.microcontainer.McaDeployer;

import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.HttpURLConnection;
import java.util.jar.JarFile;
import java.util.jar.JarEntry;
import java.util.Enumeration;
import java.io.*;

/**
 * @author Mike Ward
 * Responsible for deploying a MCA to the file system.
 */
public class DefaultMcaDeployer implements McaDeployer {
	private Configuration configuration;

	public DefaultMcaDeployer(Configuration configuration) {
		this.configuration = configuration;
		init();
	}

	private void init() {
		configuration.getWorkDir().mkdir();
		configuration.getTempDir().mkdir();
	}

	public void deploy(String context, URL mcaURL) throws IOException {
		URLConnection connection = mcaURL.openConnection();
		File sandboxDir = new File(configuration.getWorkDir(), context);
		sandboxDir.mkdir();

		if(connection instanceof JarURLConnection) {
			handleLocalMCA(sandboxDir, (JarURLConnection)connection);
		}
		else if(connection instanceof HttpURLConnection) {
			handleRemoteMCA(sandboxDir, (HttpURLConnection)connection);
		}
		else {
			throw new IOException("Unsupported URLConnection type [" + connection.getClass() + "]");
		}
	}

	protected void handleRemoteMCA(File sandboxDir, HttpURLConnection connection) throws IOException {
		File tempDir = configuration.getTempDir();

		// copy the remote MCA file to the temp dir
		String mcaFileName = sandboxDir.getName() + ".mca";
		expand(connection.getInputStream(), tempDir, mcaFileName);
		connection.disconnect();
		URL jarURL = new URL("jar:file:" + tempDir.getCanonicalPath() + "/" + mcaFileName + "!/");

		// handle as local
		handleLocalMCA(sandboxDir, (JarURLConnection)jarURL.openConnection());
	}

	protected void handleLocalMCA(File dir, JarURLConnection connection) throws IOException {
		// Expand the MCA into working directory
		connection.setUseCaches(false);
		JarFile jarFile = null;
		InputStream input = null;

		try {
			jarFile = connection.getJarFile();
			Enumeration jarEntries = jarFile.entries();

			while (jarEntries.hasMoreElements()) {
				JarEntry jarEntry = (JarEntry) jarEntries.nextElement();
				String name = jarEntry.getName();

				int last = name.lastIndexOf('/');
				if (last >= 0) {
					new File(dir, name.substring(0, last))
							.mkdirs();
				}
				if (name.endsWith("/")) {
					continue;
				}
				input = jarFile.getInputStream(jarEntry);
				expand(input, dir, name);
				input.close();
				input = null;
			}
		} catch (IOException e) {
			// problem, cleanup directory
			deleteDir(dir);
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

        if (files != null) {
			// delete all content dir (recursively)
			for (int i = 0; i < files.length; i++) {
				File file = new File(dir, files[i]);
				if (file.isDirectory()) {
					deleteDir(file);
				} else {
					file.delete();
				}
			}
		}
        dir.delete();
	}

	/**
	 * expand the content of the MCA or JAR passed in
	 */
	protected void expand(InputStream input, File docBase, String name) throws IOException {
		File file = new File(docBase, name);
		BufferedOutputStream bos = null;

		try {
			bos = new BufferedOutputStream(new FileOutputStream(file));
			byte buffer[] = new byte[2048];
			while (true) {
				int count = input.read(buffer);
				if (count <= 0) {
					break;
				}
				bos.write(buffer, 0, count);
			}
		} finally {
			bos.close();
		}
	}
}
