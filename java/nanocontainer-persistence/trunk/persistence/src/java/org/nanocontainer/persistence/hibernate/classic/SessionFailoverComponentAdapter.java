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

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.defaults.DecoratingComponentAdapter;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;


/** 
 * component adapter providing transparent session restart and management
 * this adapter serves as kind-of caching component adapter ( original CCA is 
 * unusable due to session invalidation issues ) and invalidates hibernate session
 * on error and closing. new session is obtained lazily. 
 * 
 * 
 * @author Konstantin Pribluda
 * @version $Revision$ 
 * @deprecated this component adapter is inconvenient for scripted registration
 * and can not be used with container recorder ( which is major performance drawback in web environment )
 * delegating constructable implementation of net.sf.hibernate.Session is being developed right now, 
 and will replace this adapter soon. 
 */
public class SessionFailoverComponentAdapter extends DecoratingComponentAdapter  {

	Session sessionProxy;
	
	public SessionFailoverComponentAdapter(SessionComponentAdapter sfca) {
		super(sfca);
	}
	
	/**
	 * return session wrapper
	 * 
	 */
    public Object getComponentInstance(final PicoContainer pico) throws PicoInitializationException, PicoIntrospectionException {
        if (sessionProxy == null) {
            sessionProxy = (Session) Proxy.newProxyInstance(Session.class.getClassLoader(),
                    new Class[] { Session.class },
                    new InvocationHandler() {
                        Session target = null;
                        
                        public synchronized Session getSession() {
                            if(target == null) {
                                target = (Session)getDelegate().getComponentInstance(pico);
                            }
                          return target;
                        }
                        public Object invoke(Object proxy,
                            Method method,Object[] args) throws Throwable {
                            try {
                                Object retval = method.invoke(getSession(),args);
                                if(method.getName().equals("close")) {
                                    target = null;
                                }
                                return retval;
                            } catch(InvocationTargetException ite) {
                                // we got hibernate exception kill this session
                                if(ite.getTargetException() instanceof HibernateException && target != null)  {
                                    target.clear();
                                    target.close();
                                    target = null;
                                }
                                throw ite;
                            }
                        }
                    });
        }
		return sessionProxy;
	}
	
	
}

