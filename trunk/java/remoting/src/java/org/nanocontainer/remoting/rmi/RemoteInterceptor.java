/*
 * Created by IntelliJ IDEA.
 * User: ahelleso
 * Date: 02-Mar-2004
 * Time: 16:57:02
 */
package org.nanocontainer.remoting.rmi;

import org.nanocontainer.remoting.RemotingInterceptor;

import java.lang.reflect.InvocationTargetException;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @author Neil Clayton
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public interface RemoteInterceptor extends Remote, RemotingInterceptor {
}