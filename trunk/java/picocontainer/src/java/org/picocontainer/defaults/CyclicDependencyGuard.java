/*
 * Copyright (C) 2004 Joerg Schaible
 * Created on 07.09.2004 by joehni
 */
package org.picocontainer.defaults;

/**
 * TODO Auto-generated JavaDoc
 * 
 * @author joehni
 * @since 1.1
 */
public interface CyclicDependencyGuard {

    /**
     * Derive from this class and implement this function with the functionality 
     * to observe for a dependency cycle.
     * 
     * @return a value, if the functionality result in an expression, 
     *      otherwise just return <code>null</code>
     */
    public Object run();
    
    /**
     * Call the observing function. The provided guard will hold the {@link Boolean} value.
     * If the guard is already <code>Boolean.TRUE</code> a {@link CyclicDependencyException} 
     * will be  thrown.
     * 
     * @param stackFrame the current stack frame
     * @return the result of the <code>run</code> method
     */
    public Object observe(Class stackFrame);
}