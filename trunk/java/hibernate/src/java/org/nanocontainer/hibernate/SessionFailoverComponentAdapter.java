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
import net.sf.hibernate.Transaction;

import org.picocontainer.defaults.DecoratingComponentAdapter;

import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;

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
	Transaction transactionProxy;
	
	public SessionFailoverComponentAdapter(SessionFactoryComponentAdapter sfca) {
		super(sfca);
	}
	
	/**
	 * return cached session instance. session instance will be wrapped by proxy 
	 * 
	 */
	public Object getComponentInstance() throws PicoInitializationException, PicoIntrospectionException {
		if(sessionProxy == null) {
			
		}
		
		return sessionProxy;
	}
}

