/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *****************************************************************************/
package org.picocontainer.defaults;

import org.picocontainer.PicoVisitor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Abstract PicoVisitor implementation. A generic traverse method is implemented, that 
 * accepts any object with a method named &quot;accept&quot;, that takes a 
 * {@link PicoVisitor}  as argument and and invokes it. Additionally it provides the 
 * {@link #checkTraversal()} method, that throws a {@link PicoVisitorTraversalException},
 * if currently no traversal is running.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.1
 */
public abstract class AbstractPicoVisitor implements PicoVisitor {
    private boolean traversal;
    
    /**
     * {@inheritDoc}
     * @see org.picocontainer.PicoVisitor#traverse(java.lang.Object)
     */
    public Object traverse(Object node) {
        traversal = true;
        try {
            final Method accept = node.getClass().getMethod("accept", new Class[]{PicoVisitor.class});
            accept.invoke(node, new Object[]{this});
            return Void.TYPE;
        } catch (NoSuchMethodException e) {
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
            Throwable cause = e.getTargetException();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException)cause;
            } else if (cause instanceof Error) {
                throw (Error)cause;
            }
        } finally {
            traversal = false;
        }
        throw new IllegalArgumentException(node.getClass().getName() + " is not a valid type for traversal");
    }

    /**
     * Checks the traversal flag, indicating a currently running traversal of the visitor.
     * @throws PicoVisitorTraversalException if no traversal is active.
     */
    protected void checkTraversal() {
        if (!traversal) {
            throw new PicoVisitorTraversalException(this);
        }
    }
}
