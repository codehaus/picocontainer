package org.nanocontainer.reflection.recorder;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;

/**
 * Records method calls on a {@link MutablePicoContainer}.
 * This allows to replay all invocations on a different container instance.
 *
 * This class is serializable. The original container will not be serialized
 * (for performance reasons), but the invocations will, so they can be replayed at the
 * other end of the wire.
 * 
 * @author Konstantin Pribluda ( konstantin.pribluda(at)infodesire.com )
 * @author Aslak Helles&oslash;y
 */
public class ContainerRecorder implements Serializable {

    private final List invocations = new ArrayList();
    private transient MutablePicoContainer container;

    private final InvocationHandler invocationRecorder = new InvocationRecorder();

    public ContainerRecorder(MutablePicoContainer container) {
        this.container = container;
    }

    /**
     * Creates a new proxy that will forward all method invocations to the container passed to
     * the constructor. All method invocations are recorded so that they can be replayed on a
     * different container.
     * @see #replay
     * @return a recording container proxy
     */
    public MutablePicoContainer getContainerProxy() {
        return (MutablePicoContainer) Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class[]{MutablePicoContainer.class}, invocationRecorder);
    }

    /**
     * Replay recorded invocations on target container
     * @param target container where the invocations should be replayed.
     */
    public void replay(MutablePicoContainer target) {
        for (Iterator iter = invocations.iterator(); iter.hasNext();) {
            Invocation invocation = (Invocation) iter.next();
            try {
                invocation.method.invoke(target, invocation.args);
            } catch (IllegalAccessException e) {
                throw new PicoException(e){};
            } catch (InvocationTargetException e) {
                throw new PicoException(e){};
            }
        }
    }

    private class Invocation implements Serializable {
        transient Method method;
        Object[] args;

        Invocation(Method method, Object[] args) {
            this.method = method;
            this.args = args;
        }

        private void writeObject(ObjectOutputStream out) throws IOException {
            out.defaultWriteObject();
            out.writeUTF(method.getName());
            out.writeObject(method.getDeclaringClass());
            Class[] parameterTypes = method.getParameterTypes();
            out.writeInt(parameterTypes.length);
            for (int i = 0; i < parameterTypes.length; i++) {
                out.writeObject(parameterTypes[i]);
            }
        }

        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            in.defaultReadObject();
            String methodName = in.readUTF();
            Class declaringClass = (Class) in.readObject();
            int n = in.readInt();
            Class[] parameterTypes = new Class[n];
            for(int i = 0; i < n; i++) {
                parameterTypes[i] = (Class) in.readObject();
            }
            try {
                method = declaringClass.getMethod(methodName, parameterTypes);
            } catch (NoSuchMethodException e) {
                throw new IOException("Couldn't load method " + methodName);
            }
        }
    }

    private class InvocationRecorder implements InvocationHandler, Serializable {
        /**
         * Record invocation and invoke on underlying container
         */
        public Object invoke(Object proxy, Method method, Object[] args) throws IllegalAccessException, InvocationTargetException {
            invocations.add(new Invocation(method, args));
            return method.invoke(container, args);
        }
    };

}
