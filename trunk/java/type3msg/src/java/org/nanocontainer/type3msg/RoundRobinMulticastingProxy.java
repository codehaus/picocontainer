package org.nanocontainer.type3msg;

import java.lang.reflect.Method;

/**
 * Created by IntelliJ IDEA.
 * User: skizz
 * Date: Sep 10, 2003
 * Time: 9:03:36 PM
 * To change this template use Options | File Templates.
 */
public class RoundRobinMulticastingProxy extends MulticastingProxy {

    int next = 0;

    public RoundRobinMulticastingProxy(Class theInterface) {
        super(theInterface);
    }

    public Object invoke(Object object, Method method, Object[] args) throws Throwable {
        Object item = getItem(next % getItemCount());
        next++;
        return method.invoke(item, args);
    }

}
