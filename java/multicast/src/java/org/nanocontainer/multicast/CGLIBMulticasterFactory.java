package org.nanocontainer.multicast;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class CGLIBMulticasterFactory extends AbstractMulticasterFactory {

    protected Object createProxy(ClassLoader classLoader, List objectsToAggregateCallFor, Object[] targets, InvocationInterceptor invocationInterceptor, Invoker invoker) {
        Class clazz = getProxyClass(objectsToAggregateCallFor);

        return Enhancer.create(
                clazz,
                interfaceFinder.getInterfaces(objectsToAggregateCallFor),
                new AggregatingMethodInterceptor(classLoader, targets, invocationInterceptor,invoker));
    }

    private Class getProxyClass(List objectsToAggregateCallFor) {
        return objectsToAggregateCallFor.get(0).getClass();
    }

    protected class AggregatingMethodInterceptor extends AbstractAggregatingInvocationHandler implements MethodInterceptor {
        public AggregatingMethodInterceptor(ClassLoader classLoader, Object[] tartets, InvocationInterceptor invocationInterceptor, Invoker invoker) {
            super(CGLIBMulticasterFactory.this, classLoader, tartets, invocationInterceptor, invoker);
        }

        public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            return invokeMethod(o, method, args);
        }
    }
}