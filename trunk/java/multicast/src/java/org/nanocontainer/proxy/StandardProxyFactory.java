package org.nanocontainer.proxy;

import org.picocontainer.defaults.ClassHierarchyIntrospector;

import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class StandardProxyFactory implements ProxyFactory {
    public Object createProxy(Class classOrInterface, Class[] interfaces, final InvocationInterceptor invocationInterceptor) {
        if(!classOrInterface.isInterface()) {
            Set interfaceSet = new HashSet();
            interfaceSet.addAll(ClassHierarchyIntrospector.getAllInterfaces(classOrInterface));
            if(interfaces != null) {
                interfaceSet.addAll(Arrays.asList(interfaces));
            }
            interfaces = (Class[]) interfaceSet.toArray(new Class[interfaceSet.size()]);
        }
        return Proxy.newProxyInstance(getClass().getClassLoader(), interfaces, new StandardInvocationInterceptorAdapter(invocationInterceptor));
    }

    public boolean canProxy(Class type) {
        return type.isInterface();
    }

    public boolean isProxyClass(Class clazz) {
        return Proxy.isProxyClass(clazz);
    }

}