/*****************************************************************************

 * Copyright (c) PicoContainer Organization. All rights reserved.            *

 * ------------------------------------------------------------------------- *

 * The software in this package is published under the terms of the BSD      *

 * style license a copy of which has been included with this distribution in *

 * the LICENSE.txt file.                                                     *

 *                                                                           *

 * Original code by Joerg Schaible                                           *

 *****************************************************************************/

package org.nanocontainer.ejb.rmi.mock;



import javax.ejb.EJBHome;
import javax.rmi.CORBA.PortableRemoteObjectDelegate;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.rmi.Remote;
import java.util.Map;



/**

 * Mock class for a PortableRemoteObjectDelegate.

 * This class substitutes the implementation of the JDK narrowing an EJB home interface.

 * The class is registered in a file orb.properties located in the user.home directory.

 * For unit tests just set user.home to the proper directory.

 * @author J&ouml;rg Schaible

 */

public class PortableRemoteObjectDelegateMock

        implements PortableRemoteObjectDelegate {

    

    static private Map s_narrowMap = null;

    

    /**

     * Set the narrow {@link Map}.

     * The key is the looked-up element, the value the constructor of the home interface.

     * @param narrowMap The map with the lookup values 

     * @since elsag-test 0.1

     */

    public static void setNarrowMap(final Map narrowMap) {

        s_narrowMap = narrowMap;

    }



    /**

     * Constructs a PortableRemoteObjectDelegateMock

     */

    public PortableRemoteObjectDelegateMock() {

        super();

    }



    /**

     * @see javax.rmi.CORBA.PortableRemoteObjectDelegate#exportObject(java.rmi.Remote)

     */

    public void exportObject(Remote obj) /*throws RemoteException*/ {

        return;

    }



    /**

     * @see javax.rmi.CORBA.PortableRemoteObjectDelegate#unexportObject(java.rmi.Remote)

     */

    public void unexportObject(Remote obj) /*throws NoSuchObjectException*/ {

        return;

    }



    /**

     * @see javax.rmi.CORBA.PortableRemoteObjectDelegate#toStub(java.rmi.Remote)

     */

    public Remote toStub(Remote obj) /*throws NoSuchObjectException*/ {

        return null;

    }



    /**

     * @see javax.rmi.CORBA.PortableRemoteObjectDelegate#connect(java.rmi.Remote, java.rmi.Remote)

     */

    public void connect(Remote target, Remote source) /*throws RemoteException*/ {

        return;

    }



    /**

     * @see javax.rmi.CORBA.PortableRemoteObjectDelegate#narrow(java.lang.Object, java.lang.Class)

     */

    public Object narrow(Object narrowFrom, Class narrowTo) throws ClassCastException {

        final Constructor ctor = (Constructor)s_narrowMap.get(narrowFrom);

        if (ctor != null) {

            final ClassCastException cce = new ClassCastException(ctor.getDeclaringClass().getName());

            try {

                if (!Modifier.isInterface(narrowTo.getModifiers())) {

                    cce.initCause(new IllegalArgumentException(narrowTo.getName() + " should be an interface"));

                } else if (EJBHome.class.isAssignableFrom(narrowTo)

                        && narrowTo.isAssignableFrom(ctor.getDeclaringClass())) {

                    return ctor.newInstance(null);

                }

            } catch (IllegalArgumentException e) {

                cce.initCause(e);

            } catch (InstantiationException e) {

                cce.initCause(e);

            } catch (IllegalAccessException e) {

                cce.initCause(e);

            } catch (InvocationTargetException e) {

                cce.initCause(e);

            }

            throw cce;

        }

        return null;

    }



}

