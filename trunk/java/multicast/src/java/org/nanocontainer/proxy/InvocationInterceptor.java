package org.nanocontainer.proxy;

import java.lang.reflect.Method;
import java.io.Serializable;

public interface InvocationInterceptor extends Serializable {
    Object intercept(Object proxy, Method method, Object[] args) throws Throwable;
}