package org.nanocontainer.multicast;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class CGLIBMulticasterFactory extends AbstractMulticasterFactory {

    protected Object createProxy(ClassLoader classLoader, Class type, List objectsToAggregateCallFor, Object[] targets, InvocationInterceptor invocationInterceptor, Invoker invoker) {
        return Enhancer.create(
                type,
                interfaceFinder.getInterfaces(objectsToAggregateCallFor),
                new AggregatingMethodInterceptor(classLoader, targets, invocationInterceptor,invoker));
    }

    public boolean canMulticast(Class type) {
        int modifiers = type.getModifiers();
        return Modifier.isPublic(modifiers) && !Modifier.isAbstract(modifiers);
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