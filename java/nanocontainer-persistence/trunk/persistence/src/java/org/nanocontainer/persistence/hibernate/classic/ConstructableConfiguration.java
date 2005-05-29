/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the license.html file.                                                    *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/
package org.nanocontainer.persistence.hibernate.classic;

import net.sf.hibernate.cfg.Configuration;
import net.sf.hibernate.HibernateException;

import java.net.URL;
import java.io.File;
import org.w3c.dom.Document;

/**
 * constructable hibernate configuration. not a rocket science, just a wrapper aroung
 * various configure() methods... see respective hibernate javadocs.
 * @author Konstantin Pribluda
 * @version $Revision$
 */
public class ConstructableConfiguration extends Configuration {
	
	public ConstructableConfiguration() throws HibernateException {
		configure();
	}
	
	public ConstructableConfiguration(URL url) throws HibernateException {
		configure(url);
	}
	public ConstructableConfiguration(String resource) throws HibernateException {
		configure(resource);
	}
	
	public ConstructableConfiguration(File configFile) throws HibernateException {
		configure(configFile);
	}
	
	
	public ConstructableConfiguration(Document document)  throws HibernateException {
		configure(document);
	}
}
