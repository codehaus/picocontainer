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

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;
import org.picocontainer.Parameter;

import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoVerificationException;
import org.picocontainer.defaults.CyclicDependencyException;
import org.picocontainer.defaults.UnsatisfiableDependenciesException;
import org.picocontainer.defaults.ConstantParameter;
import org.picocontainer.defaults.ComponentParameter;

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
public class SessionFactoryComponentAdapter implements ComponentAdapter {
	
	Object componentKey = null;
	Parameter configurationParameter = null;
	PicoContainer container = null;
	ComponentAdapter hibernateConfigurationAdapter = null;
	boolean verifying = false;
	
	public SessionFactoryComponentAdapter() {
		this(null,null);
	}
	
	public SessionFactoryComponentAdapter(Object componentKey) {
		this(componentKey,null);
	}
	
	public SessionFactoryComponentAdapter(Object componentKey,Parameter parameter) {
		this.componentKey = componentKey == null? SessionFactory.class : componentKey;
		this.configurationParameter = parameter == null ? new ComponentParameter(Configuration.class) : parameter;
	}
	
	
	public Object getComponentKey() {
		return componentKey;
	}
	
	/**
	 * we provide net.sf.hibernate.SessionFactory. nothing else
	 */
	public Class  getComponentImplementation() {
		return SessionFactory.class;
	}
	
	
	public Object getComponentInstance() throws   PicoInitializationException, PicoIntrospectionException {
		verify();
		try {
		return ((Configuration)hibernateConfigurationAdapter.getComponentInstance()).buildSessionFactory();
		} catch(HibernateException he) {
			throw new PicoInitializationException(he);
		}
	}
	
	public PicoContainer getContainer() {
		return container;	
	}
	
	public void setContainer(PicoContainer picoContainer) {
		this.container = picoContainer;
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
