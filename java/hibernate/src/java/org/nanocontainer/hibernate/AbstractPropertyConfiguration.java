/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the license.html file.                                                    *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/
package org.nanocontainer.hibernate;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.MappingException;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.cfg.Configuration;

import java.io.IOException;
import java.util.Properties;

/**
 * abstract base class for property based hibernate configuration. concrete implementation 
 * shall register persistent classes. 
 *
 * @author    Konstantin Pribluda ( konstantin.pribluda[at]infodesire.com )
 * @version   $Revision$
 */
public abstract class AbstractPropertyConfiguration extends Configuration implements SessionFactoryProvider {
	
	SessionFactory sessionFactory;
    /**
     * configure from properties contained on class path
     *
     * @param properties              path to your properties on classpath
     * @exception HibernateException  if hibernate barfs
     * @exception IOException         if your properties path is suboptimal 
     */
    protected AbstractPropertyConfiguration(String properties) throws HibernateException, IOException {
        addProperties(loadProperties(properties));
        registerClasses();
		sessionFactory = buildSessionFactory();
    }

    /**
     * configure from properties obtained elswhere
     *
     * @param properties              properties obtained elswhere
     * @exception HibernateException  if hibernate barfs...
     */
    protected AbstractPropertyConfiguration(Properties properties) throws HibernateException {
        addProperties(properties);
        registerClasses();
		sessionFactory = buildSessionFactory();
    }


	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}
    /**
     * we do not allow to instantiate us without proper properties
     */
    private AbstractPropertyConfiguration() {
    }


    /**
     * load properties from classpath
     *
     * @param name             path to properties
     * @return                 properties we have loaded
     * @exception IOException  if properties can not be loaded
     */
     Properties loadProperties(String name) throws IOException {
        Properties hibernateProperties = new Properties();
        hibernateProperties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(name));
        return hibernateProperties;
    }


    /**
     * register application classes. implementors have to override this. 
     *
     * @exception MappingException  if hibernate finds your mappings unworthy
     */
    protected abstract void registerClasses() throws MappingException;

}
