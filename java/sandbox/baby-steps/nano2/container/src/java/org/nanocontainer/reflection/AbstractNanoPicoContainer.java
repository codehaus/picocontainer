/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Paul Hammant                                             *
 *****************************************************************************/

package org.nanocontainer.reflection;

import java.io.Serializable;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.nanocontainer.ClassPathElement;
import org.nanocontainer.DefaultNanoContainer;
import org.nanocontainer.NanoContainer;
import org.nanocontainer.NanoPicoContainer;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.alternatives.AbstractDelegatingMutablePicoContainer;

/**
 * A base class for NanoPicoContainers. As well as the functionality indicated by the interface it
 * implements, extenders of this class will have named child component capability.
 *
 * @author Paul Hammant
 * @version $Revision$
 */
public abstract class AbstractNanoPicoContainer extends AbstractDelegatingMutablePicoContainer implements NanoPicoContainer, Serializable {

    protected Map namedChildContainers = new HashMap();

    // Serializable cannot be cascaded into DefaultNanoContainer's referenced classes
    // need to implement custom Externalisable regime.
    protected transient NanoContainer container;


    protected AbstractNanoPicoContainer(MutablePicoContainer delegate, ClassLoader classLoader) {
        super(delegate);
        container = new DefaultNanoContainer(classLoader, delegate);
    }

    public final Object getComponent(Object componentKeyOrType) throws PicoException {

        Object instance = getDelegate().getComponent(componentKeyOrType);

        if (instance != null) {
            return instance;
        }

        ComponentAdapter componentAdapter = null;
        if (componentKeyOrType.toString().startsWith("*")) {
            String candidateClassName = componentKeyOrType.toString().substring(1);
            Collection cas = getComponentAdapters();
            for (Iterator it = cas.iterator(); it.hasNext();) {
                ComponentAdapter ca = (ComponentAdapter) it.next();
                Object key = ca.getComponentKey();
                if (key instanceof Class && candidateClassName.equals(((Class) key).getName())) {
                    componentAdapter = ca;
                    break;
                }
            }
        }
        if (componentAdapter != null) {
            return componentAdapter.getComponentInstance(this);
        } else {
            return getComponentInstanceFromChildren(componentKeyOrType);
        }
    }

    private Object getComponentInstanceFromChildren(Object componentKey) {
        String componentKeyPath = componentKey.toString();
        int ix = componentKeyPath.indexOf('/');
        if (ix != -1) {
            String firstElement = componentKeyPath.substring(0, ix);
            String remainder = componentKeyPath.substring(ix + 1, componentKeyPath.length());
            Object o = getNamedContainers().get(firstElement);
            if (o != null) {
                MutablePicoContainer child = (MutablePicoContainer) o;
                return child.getComponent(remainder);
            }
        }
        return null;
    }

    public final MutablePicoContainer makeChildContainer() {
        return makeChildContainer("containers" + namedChildContainers.size());
    }

    /**
     * Makes a child container with the same basic characteristics of <tt>this</tt>
     * object (ComponentAdapterFactory, PicoContainer type, LifecycleManager, etc)
     * @param name the name of the child container
     * @return The child MutablePicoContainer
     */
    public MutablePicoContainer makeChildContainer(String name) {
        AbstractNanoPicoContainer child = createChildContainer();
        MutablePicoContainer parentDelegate = getDelegate();
        parentDelegate.removeChildContainer(child.getDelegate());
        parentDelegate.addChildContainer(child);
        namedChildContainers.put(name, child);
        return child;
    }

    protected abstract AbstractNanoPicoContainer createChildContainer();

    public boolean removeChildContainer(PicoContainer child) {
        boolean result = getDelegate().removeChildContainer(child);
        Iterator children = namedChildContainers.entrySet().iterator();
        while (children.hasNext()) {
            Map.Entry e = (Map.Entry) children.next();
            PicoContainer pc = (PicoContainer) e.getValue();
            if (pc == child) {
                children.remove();
            }
        }
        return result;
    }

    protected final Map getNamedContainers() {
        return namedChildContainers;
    }

    public Object getComponent(String componentType) {
        return container.getComponent(componentType);
    }

    public MutablePicoContainer addDecoratingPicoContainer(Class picoContainerClass) {
        return container.addDecoratingPicoContainer(picoContainerClass);
    }


    public ClassPathElement addClassLoaderURL(URL url) {
        return container.addClassLoaderURL(url);
    }

    public ComponentAdapter registerComponent(Object componentImplementationOrInstance) throws PicoRegistrationException, PicoIntrospectionException {
        //QQQ
        return container.registerComponent(componentImplementationOrInstance);
    }

    public ComponentAdapter registerComponent(Object key, String componentImplementationClassName) {
        return container.registerComponent(key, componentImplementationClassName);
    }

    public ComponentAdapter registerComponent(Object key, String componentImplementationClassName, Parameter[] parameters) {
        return container.registerComponent(key, componentImplementationClassName, parameters);
    }

    public ComponentAdapter registerComponent(Object key, String componentImplementationClassName, String[] parameterTypesAsString, String[] parameterValuesAsString) throws PicoRegistrationException, PicoIntrospectionException, ClassNotFoundException {
        return container.registerComponent(key, componentImplementationClassName, parameterTypesAsString, parameterValuesAsString);
    }

    public ComponentAdapter registerComponent(String componentImplementationClassName, String[] parameterTypesAsString, String[] parameterValuesAsString) throws PicoRegistrationException, PicoIntrospectionException, ClassNotFoundException {
        return container.registerComponent(componentImplementationClassName, parameterTypesAsString, parameterValuesAsString);
    }


    //TODO Should this method be the NanoContainer interface only?
    public MutablePicoContainer getPico() {
        return this;
    }

    public ClassLoader getComponentClassLoader() {
        return container.getComponentClassLoader();
    }

    public boolean addChildContainer(PicoContainer child) {
        boolean result = getDelegate().addChildContainer(child);


        namedChildContainers.put("containers" + namedChildContainers.size(), child);
        return result;
    }

    public void addChildContainer(String name, PicoContainer child) {

        super.addChildContainer(child);

        namedChildContainers.put(name, child);
    }

}
