/*
 * Copyright (C) 2005 Jörg Schaible
 * Created on 16.08.2005 by Jörg Schaible
 */
package org.picocontainer.alternatives;

import org.picocontainer.ComponentMonitor;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.defaults.MethodCallingVisitor;

import java.lang.reflect.Method;

/**
 * A PicoVisitor for lifecycle methods. Any method call fires the appropriate events:
 * <ul>
 * <li>{@link ComponentMonitor#invoking(Method, Object)}</li>
 * <li>{@link ComponentMonitor#invoked(Method, Object, long)}</li>
 * <li>{@link ComponentMonitor#invocationFailed(Method, Object, Exception)}</li>
 * </ul>
 * 
 * @author J&ouml;rg Schaible
 * @author Mauro Talevi
 * @author Aslak Helles&oslash;y
 * @since 1.2
 */
public class LifecycleVisitor extends MethodCallingVisitor {

    private transient ThreadLocal containerGuard;
    private final boolean isLifecycleManagerCompatible;
    private final ComponentMonitor componentMonitor;

    /**
     * Construct a LifecycleVisitor.
     * 
     * @param method
     * @param ofType
     * @param visitInInstantiationOrder
     * @param componentMonitor the {@link ComponentMonitor} used to fire the invocation events
     * @param isLifecycleManagerCompatible 
     * @throws NullPointerException if <tt>method</tt>, <tt>ofType</tt>, or <tt>componentMonitor</tt> is
     *             <code>null</code>
     */
    protected LifecycleVisitor(Method method, Class ofType, boolean visitInInstantiationOrder, ComponentMonitor componentMonitor, boolean isLifecycleManagerCompatible) {
        super(method, ofType, null, visitInInstantiationOrder);
        if (componentMonitor == null) {
            throw new NullPointerException();
        }
        this.componentMonitor = componentMonitor;
        this.isLifecycleManagerCompatible = isLifecycleManagerCompatible;
        this.containerGuard = new ThreadLocal();
    }

    /**
     * Construct a LifecycleVisitor.
     * 
     * @param method
     * @param ofType
     * @param visitInInstantiationOrder
     * @param componentMonitor the {@link ComponentMonitor} used to fire the invocation events
     * @throws NullPointerException if <tt>method</tt>, <tt>ofType</tt>, or <tt>componentMonitor</tt> is
     *             <code>null</code>
     */
    public LifecycleVisitor(Method method, Class ofType, boolean visitInInstantiationOrder, ComponentMonitor componentMonitor) {
        this(method, ofType, visitInInstantiationOrder, componentMonitor, true);
    }
    
    public void visitContainer(PicoContainer pico) {
        if (isLifecycleManagerCompatible) {
            if (containerGuard.get() != null) {
                return;
            }
            containerGuard.set(pico);
        }
        super.visitContainer(pico);
        containerGuard.set(null);
    }

    protected Object invoke(final Object target) {
        final Method method = getMethod();
        try {
            componentMonitor.invoking(method, target);
            final long startTime = System.currentTimeMillis();
            super.invoke(target);
            componentMonitor.invoked(method, target, System.currentTimeMillis() - startTime);
        } catch (final PicoIntrospectionException e) {
            componentMonitor.invocationFailed(method, target, e.getCause() instanceof Exception ? (Exception)e.getCause() : e);
            throw e;
        }
        return Void.TYPE;
    }

}
