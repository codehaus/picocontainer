package org.nanocontainer.type3msg;

import org.picocontainer.PicoException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MulticastingProxy implements InvocationHandler {

    private Class theInterface;
    private List items;

    public MulticastingProxy(Class theInterface) {
        this.theInterface = theInterface;
        items = new ArrayList();
    }

    public void add(Object item) throws PicoException {
        if (!theInterface.isAssignableFrom(item.getClass())) {
            throw new MulticastingPicoException(
                    "The item " + item.getClass().getName() + " is not an instance of " + theInterface.getName());
        }
        items.add(item);
    }

    public Object getProxy() {
        return Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{theInterface}, this);
    }

    public Object invoke(Object object, Method method, Object[] args) throws Throwable {
        Iterator iterator = items.iterator();
        while (iterator.hasNext()) {
            Object item = iterator.next();
            method.invoke(item, args);
        }
        return null;
    }

    protected Object getItem(int index) {
        return items.get(index);
    }

    protected int getItemCount() {
        return items.size();
    }
}
