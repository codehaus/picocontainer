/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
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
        int itemCount = getItemCount();
        if(itemCount == 0) {
            throw new RuntimeException("No items have been added. Don't know where to forward the invocation of " + method.getName());
        }
        Object item = getItem(next % itemCount);
        next++;
        return method.invoke(item, args);
    }

}
