package org.nanocontainer.multicast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class AbstractAggregatingInvocationHandler {
    private final MulticasterFactory multicasterFactory;
    private final ClassLoader classLoader;
    private final Object[] targets;
    private final InvocationInterceptor invocationInterceptor;
    private final Invoker invoker;

    public AbstractAggregatingInvocationHandler(MulticasterFactory multicasterFactory, ClassLoader classLoader, Object[] targets, InvocationInterceptor invocationInterceptor, Invoker invoker) {
        this.multicasterFactory = multicasterFactory;
        this.classLoader = classLoader;
        this.targets = targets;
        this.invocationInterceptor = invocationInterceptor;
        this.invoker = invoker;
    }

    protected Object invokeMethod(Object proxy, Method method, Object[] args) throws Throwable {
        Class declaringClass = method.getDeclaringClass();
        if (declaringClass.equals(Object.class)) {
            if (method.equals(org.picocontainer.defaults.InterfaceFinder.hashCode)) {
                // Return the hashCode of ourself, as Proxy.newProxyInstance() may
                // return cached proxies. We want a unique hashCode for each created proxy!
                return new Integer(System.identityHashCode(AbstractAggregatingInvocationHandler.this));
            }
            if (method.equals(org.picocontainer.defaults.InterfaceFinder.equals)) {
                return new Boolean(proxy == args[0]);
            }
            // If it's any other method defined by Object, call on ourself.
            return method.invoke(AbstractAggregatingInvocationHandler.this, args);
        } else {
            return invokeOnTargetsOfSameTypeAsDeclaringClass(declaringClass, targets, method, args);
        }
    }

    private Object invokeOnTargetsOfSameTypeAsDeclaringClass(Class declaringClass, Object[] targets, Method method, Object[] args) throws Throwable, InstantiationException {

        List results = new ArrayList();
        invoker.invoke(targets, declaringClass, method, args, results, invocationInterceptor);

        Object result;
        Class returnType = method.getReturnType();
        if (results.size() == 1) {
            // Got exactly one result. Just return that.
            result = results.get(0);
        } else if (returnType.isInterface()) {
            // We have two or more results
            // We can make a new proxy that aggregates all the results.
            //Class[] resultInterfaces = getInterfaces(results.toArray());
            result = multicasterFactory.createComponentMulticaster(classLoader,
                    results,
                    true,
                    invocationInterceptor,
                    invoker);
        } else if (returnType.isPrimitive()) {
            // Not great. We should probably handle the differently. Some kind of add.
            result = results.get(0);
        } else {
            // Got multiple results that can't be wrapped in a proxy. Try to instantiate a default object.
            result = returnType.equals(Void.TYPE) ? null : returnType.newInstance();
        }

        return result;
    }
}