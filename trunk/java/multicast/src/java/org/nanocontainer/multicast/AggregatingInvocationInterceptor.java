package org.nanocontainer.multicast;

import org.nanocontainer.proxy.InvocationInterceptor;
import org.nanocontainer.proxy.ProxyFactory;
import org.picocontainer.defaults.ClassHierarchyIntrospector;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @deprecated Will be replaced by {@link org.nanocontainer.proxytoys.Multicaster}
 * @author Aslak Helles&oslash;y
 * @author Chris Stevenson
 * @version $Revision$
 */
public class AggregatingInvocationInterceptor implements InvocationInterceptor {
    private final MulticasterFactory multicasterFactory;
    private final Object[] targets;
    private final Invoker invoker;
    private final ProxyFactory proxyFactory;

    public AggregatingInvocationInterceptor(MulticasterFactory multicasterFactory, Object[] targets, Invoker invoker, ProxyFactory proxyFactory) {
        this.multicasterFactory = multicasterFactory;
        this.targets = targets;
        this.invoker = invoker;
        this.proxyFactory = proxyFactory;
    }

    public Object intercept(Object proxy, Method method, Object[] args) throws Throwable {
        Class declaringClass = method.getDeclaringClass();
        if (declaringClass.equals(Object.class)) {
            if (method.equals(ClassHierarchyIntrospector.hashCode)) {
                // Return the hashCode of ourself, as Proxy.newProxyInstance() may
                // return cached proxies. We want a unique hashCode for each created proxy!
                return new Integer(System.identityHashCode(AggregatingInvocationInterceptor.this));
            }
            if (method.equals(ClassHierarchyIntrospector.equals)) {
                return new Boolean(proxy == args[0]);
            }
            // If it's any other method defined by Object, call on ourself.
            return method.invoke(AggregatingInvocationInterceptor.this, args);
        } else {
            return invokeOnTargetsOfSameTypeAsDeclaringClass(declaringClass, targets, method, args);
        }
    }

    private Object invokeOnTargetsOfSameTypeAsDeclaringClass(Class declaringClass, Object[] targets, Method method, Object[] args) throws Throwable {

        List results = new ArrayList();
        invoker.invoke(targets, declaringClass, method, args, results);

        Object result;
        Class methodReturnType = method.getReturnType();
        Class actualReturnType = null;
        if(methodReturnType.isInterface()) {
            actualReturnType = methodReturnType;
        } else {
            actualReturnType = ClassHierarchyIntrospector.getMostCommonSuperclass(results.toArray());
        }
        if (results.size() == 1) {
            // Got exactly one result. Just return that.
            result = results.get(0);
        } else if (proxyFactory.canProxy(actualReturnType)) {
            // We have two or more results
            // We can make a new proxy that aggregates all the results.
            //Class[] resultInterfaces = getInterfaces(results.toArray());
            result = multicasterFactory.createComponentMulticaster(actualReturnType,
                    null, 
                    results,
                    true,
                    invoker);
        } else if (methodReturnType.isPrimitive()) {
            // Not great. We should probably handle them differently. Some kind of add.
            result = results.get(0);
        } else {
            // Got multiple results that can't be wrapped in a proxy. Try to instantiate a default object.
            result = methodReturnType.equals(Void.TYPE) ? null : methodReturnType.newInstance();
        }

        return result;
    }
}