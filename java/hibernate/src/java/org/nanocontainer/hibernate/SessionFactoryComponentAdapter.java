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

import org.picocontainer.PicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.ComponentAdapter;

import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoVerificationException;

import org.picocontainer.defaults.CyclicDependencyException;
import org.picocontainer.defaults.UnsatisfiableDependenciesException;
import org.picocontainer.defaults.ComponentParameter;
import org.picocontainer.defaults.AbstractComponentAdapter;

import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.cfg.Configuration;
import net.sf.hibernate.HibernateException;

import java.util.Collections;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * component adapter providing  hibernate session factory.
 * @author konstantin pribluda
 * @version $Revision$
 */
public class SessionFactoryComponentAdapter extends AbstractComponentAdapter {
	
	Parameter configurationParameter = null;
	ComponentAdapter hibernateConfigurationAdapter = null;
	boolean verifying = false;
	
	/**
	 * construct adapter with net.sf.hibernate.SessionFactory.class as key 
	 * and dependecy to net.sf.hibernate.cfg.Configuration.class
	 */
	public SessionFactoryComponentAdapter() {
		this(SessionFactory.class,null);
	}
	
	/**
	 * construct component adapter with specified key and dependecy to 
	 * net.sf.hibernate.cfg.Configuration.class
	 */
	public SessionFactoryComponentAdapter(Object componentKey) {
		this(componentKey,null);
	}
	
	/**
	 * construct component adapter with given key and specified parameter.
	 * in case null parameter is supplied use dependency to 
	 * net.sf.hibernate.cfg.Configuration.class
	 */
	public SessionFactoryComponentAdapter(Object componentKey,Parameter parameter) {
		super(componentKey,SessionFactory.class);
		this.configurationParameter = parameter == null ? new ComponentParameter() : parameter;
	}
	
	
	/** 
	 * obtain session factory instance if possible
	 */
	public Object getComponentInstance() throws   PicoInitializationException, PicoIntrospectionException {
		verify();
		try {
		return ((Configuration)hibernateConfigurationAdapter.getComponentInstance()).buildSessionFactory();
		} catch(HibernateException he) {
			throw new PicoInitializationException(he);
		}
	}

	public void verify() throws PicoVerificationException {
		try {
			if(verifying) {
				throw new CyclicDependencyException(new Class[] { Configuration.class } );
			}
			verifying = true;
			HashSet unsatisfiableDependencies = new HashSet();
			unsatisfiableDependencies.add(Configuration.class);
			
			hibernateConfigurationAdapter = configurationParameter.resolveAdapter(getContainer(),Configuration.class);
			
			if(hibernateConfigurationAdapter == null) {
				throw new  UnsatisfiableDependenciesException(this,unsatisfiableDependencies);
			}


		} finally {
			verifying = false;
		}
	}
}
