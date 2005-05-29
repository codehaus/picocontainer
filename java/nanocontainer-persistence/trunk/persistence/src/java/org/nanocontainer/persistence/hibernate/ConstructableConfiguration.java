/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the license.html file.                                                    *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package org.nanocontainer.persistence.hibernate;

import java.io.File;
import java.net.URL;

import org.hibernate.HibernateException;
import org.hibernate.cfg.Configuration;
import org.w3c.dom.Document;

/**
 * Constructable hibernate configuration. not a rocket science, just a wrapper aroung various
 * configure() methods... see respective hibernate javadocs.
 * 
 * @author Jose Peleteiro <juzepeleteiro@intelli.biz>
 * @version $Revision$
 */
public class ConstructableConfiguration extends Configuration {

    public ConstructableConfiguration() throws HibernateException {
        this.configure();
    }

    public ConstructableConfiguration(URL url) throws HibernateException {
        this.configure(url);
    }

    public ConstructableConfiguration(String resource) throws HibernateException {
        this.configure(resource);
    }

    public ConstructableConfiguration(File configFile) throws HibernateException {
        this.configure(configFile);
    }

    public ConstructableConfiguration(Document document) throws HibernateException {
        this.configure(document);
    }

}
