/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Jon Tirsen                        *
 *****************************************************************************/

package org.picocontainer.parameters;

import java.io.Serializable;

import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoVisitor;


/**
 * A ConstantParameter should be used to pass in "constant" arguments to constructors. This
 * includes {@link String}s,{@link Integer}s or any other object that is not registered in
 * the container.
 *
 * @author Jon Tirs&eacute;n
 * @author Aslak Helles&oslash;y
 * @author J&ouml;rg Schaible
 * @author Thomas Heller
 * @author Konstantin Pribluda
 */
@SuppressWarnings("serial")
public class ConstantParameter<T>  implements Parameter<T>, Serializable  {

    private final T value;

    public ConstantParameter(T value) {
        this.value = value;
    }

    public T resolveInstance(PicoContainer container) {
        return value;
    }

    public boolean isResolvable(PicoContainer container) {
            return true;
    }


    /**
     * Visit the current {@link Parameter}.
     *
     * @see org.picocontainer.Parameter#accept(org.picocontainer.PicoVisitor)
     */
    public void accept(final PicoVisitor visitor) {
        visitor.visitParameter(this);
    }


    /**
     * ...we are always  fine
     */
	public void verify(PicoContainer container) {
		// actually a no-op
	}

}
