/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package org.picocontainer.behaviors;

import java.io.Serializable;

import org.picocontainer.Behavior;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.ObjectReference;

/**
 * <p>
 * {@link ComponentAdapter} implementation that caches the component instance.
 * </p>
 * <p>
 * This adapter supports components with a lifecycle, as it is a
 * {@link Behavior lifecycle manager} which will apply the delegate's
 * {@link org.picocontainer.LifecycleStrategy lifecycle strategy} to the cached
 * component instance. The lifecycle state is maintained so that the component
 * instance behaves in the expected way: it can't be started if already started,
 * it can't be started or stopped if disposed, it can't be stopped if not
 * started, it can't be disposed if already disposed.
 * </p>
 * 
 * @author Mauro Talevi
 */
public class Cached<T> extends Stored<T> {

	public Cached(ComponentAdapter delegate) {
		this(delegate, new SimpleReference());
	}

	public Cached(ComponentAdapter delegate, ObjectReference instanceReference) {
		super(delegate, instanceReference);
	}

	/**
	 * @author Aslak Helles&oslash;y
	 */
	public static class SimpleReference<T> implements ObjectReference<T>,
			Serializable {
		private T instance;

		public SimpleReference() {
		}

		public T get() {
			return instance;
		}

		public void set(T item) {
			this.instance = item;
		}
	}

	public String toString() {
		return "Cached:" + super.toString();
	}

}
