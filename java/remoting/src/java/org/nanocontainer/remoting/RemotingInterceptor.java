package org.nanocontainer.remoting;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public interface RemotingInterceptor {
    Object invoke(Invocation invocation) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, RemoteException;
}