package org.nanocontainer.proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.Factory;
import net.sf.cglib.proxy.Proxy;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * @deprecated Will be replaced by http://proxytoys.cocehaus.org/
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class CGLIBProxyFactory implements ProxyFactory {
    private static final Map boxedClasses = new HashMap();

    static {
        boxedClasses.put(Integer.TYPE, Integer.class);
        boxedClasses.put(Byte.TYPE, Byte.class);
        boxedClasses.put(Short.TYPE, Short.class);
        boxedClasses.put(Long.TYPE, Long.class);
        boxedClasses.put(Double.TYPE, Double.class);
        boxedClasses.put(Character.TYPE, Character.class);
        boxedClasses.put(Float.TYPE, Float.class);
    }

    public Object createProxy(Class classOrInterface, Class[] interfaces, final InvocationInterceptor invocationInterceptor) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(classOrInterface);
        enhancer.setInterfaces(interfaces);
        enhancer.setCallback(new CGLIBInvocationInterceptorAdapter(invocationInterceptor));
        Constructor constructor = getConstructor(classOrInterface);
        Class[] params = constructor.getParameterTypes();
        Object[] args = new Object[params.length];
        for (int i = 0; i < args.length; i++) {
            args[i] = createNullArg(params[i]);
        }
        return enhancer.create(params, args);
    }

    private Constructor getConstructor(Class clazz) {
        Constructor constructor = null;
        try {
            constructor = clazz.getConstructor(null);
        } catch (NoSuchMethodException e) {
            constructor = clazz.getConstructors()[0];

        }
        return constructor;
    }

    private Object createNullArg(Class param) {
        if (param.isPrimitive()) {
            Class boxedClass = (Class) boxedClasses.get(param);
            try {
                Field field = boxedClass.getField("MIN_VALUE");
                return field.get(null);
            } catch (NoSuchFieldException e) {
                throw new InternalError();
            } catch (IllegalAccessException e) {
                throw new InternalError();
            }
        } else {
            return null;
        }
    }

    public boolean canProxy(Class type) {
        int mofifiers = type.getModifiers();
        return Modifier.isPublic(mofifiers) && !Modifier.isAbstract(mofifiers) && !Modifier.isFinal(mofifiers);
    }

    public boolean isProxyClass(Class clazz) {
        return Factory.class.isAssignableFrom(clazz) || Proxy.isProxyClass(clazz);
    }

}