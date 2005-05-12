package org.nanocontainer.remoting;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface NanoNaming extends Remote {
    Object lookup(ByRefKey key) throws RemoteException;
}