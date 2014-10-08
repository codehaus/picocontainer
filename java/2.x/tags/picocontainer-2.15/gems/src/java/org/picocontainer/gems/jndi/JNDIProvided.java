/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/

package org.picocontainer.gems.jndi;

import java.io.Serializable;
import java.lang.reflect.Type;

import javax.naming.NamingException;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoVisitor;

/**
 * Represents dependency provided via JNDI. This dependency is not 
 * to be managed by container at all, so there is no lifecycle, no 
 * monitoring etc. 
 * @author Konstantin Pribluda
 *
 */
@SuppressWarnings("serial")
public class JNDIProvided<T> implements ComponentAdapter<T>, Serializable {

	private JNDIObjectReference<T> jndiReference;	
	private Class<T> type;
    private Object componentKey;
	
	/**
	 * Create adapter with specified key and reference
	 * @param componentKey component key
	 * @param reference JNDI reference storing component
     * @param type the type that the JNDIObjectReference will return.
	 */
	public JNDIProvided(final Object componentKey,final JNDIObjectReference<T> reference, final Class<T> type) {
		this.componentKey = componentKey;
		this.jndiReference = reference;
		this.type = type;
	}
	
	/**
	 * Create adapter with JNDI reference. referenced object class will be
	 * takes as key
	 * @param reference JNDI reference storing component
     * @param type the type that the JNDIObjectReference will return.
	 */
	public JNDIProvided(final JNDIObjectReference<T> reference, Class<T> type) {
		this(reference.get().getClass(),reference, type);
	}
	
	/**
	 * Create adapter based on JNDI name. I leave this unchecked because
	 * type is really not known at this time
	 * @param jndiName name to be used
     * @param type the type that the JNDIObjectReference will return.
     * @throws NamingException will be thrown if something goes
	 * wrong in JNDI
	 */
	@SuppressWarnings("unchecked")
	public JNDIProvided(final String jndiName, Class<T> type) throws NamingException {
		this(new JNDIObjectReference(jndiName), type);
	}
	
	public Object getComponentKey() {	
		return componentKey;
	}

	@SuppressWarnings("unchecked")
	public Class<? extends T> getComponentImplementation() {
		return type;
	}

    public T getComponentInstance(final PicoContainer container) throws PicoCompositionException {
        return getComponentInstance(container, null);
    }

    /**
	 * Retrieve instance out of JNDI
	 */
	public T getComponentInstance(final PicoContainer container, final Type into)
			throws PicoCompositionException {
		return  jndiReference.get();
	}

	/**
	 * we have nothing to verify here
     */
	public void verify(final PicoContainer container) throws PicoCompositionException {
	}

	/**
	 * As there is no puprose of proceeding further down,
	 * we do nothing here
	 */
	public void accept(final PicoVisitor visitor) {
	}

    public ComponentAdapter<T> getDelegate() {
        return null;
    }

    public <U extends ComponentAdapter> U findAdapterOfType(final Class<U> adapterType) {
        return null;
    }

    public String getDescriptor() {
        return "JNDI(" + jndiReference.getName() + ")";
    }

}
