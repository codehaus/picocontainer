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

import net.sf.hibernate.Session;
import net.sf.hibernate.HibernateException;

import org.picocontainer.defaults.DecoratingComponentAdapter;

import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;

import java.lang.reflect.Proxy;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;


/** 
 * component adapter providing transparent session restart and management
 * also takes care that transactions do not overlap. this adapter serves as kind-of 
 * caching component adapter ( original CCA is unusable due to session invalidation issues )
 * 
 * 
 * @author Konstantin Pribluda
 * @version $Revision$ 
 */
public class SessionFailoverComponentAdapter extends DecoratingComponentAdapter  {

	Session sessionProxy;
	
	public SessionFailoverComponentAdapter(SessionComponentAdapter sfca) {
		super(sfca);
	}
	
	/**
	 * return cached session instance. session instance will be wrapped by proxy 
	 * 
	 */
	public Object getComponentInstance() throws PicoInitializationException, PicoIntrospectionException {
		if(sessionProxy == null) {
			final Session session = (Session)super.getComponentInstance();
			
			sessionProxy = (Session) Proxy.newProxyInstance(Session.class.getClassLoader(),
				new Class[] { Session.class },
				new InvocationHandler() {
					Session target = session;
					
					public Object invoke(Object proxy,
						Method method,Object[] args) throws Throwable {
						try {
							if(method.getName().equals("close")) {
								sessionProxy = null;
							}
							return method.invoke(target,args);
						} catch(InvocationTargetException ite) {
							// we got hibernate exception kill this session
							if(ite.getCause() instanceof HibernateException)  {
								target.clear();
								target.close();
								sessionProxy = null;
							}
							throw ite;
						}
					}
				});
				
		}
		
		return sessionProxy;
	}
	
	
}

