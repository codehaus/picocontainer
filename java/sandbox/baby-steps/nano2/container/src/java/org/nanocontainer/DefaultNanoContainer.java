/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Paul Hammant                                             *
 *****************************************************************************/

package org.nanocontainer;

import java.io.Serializable;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PermissionCollection;

import org.picocontainer.ComponentMonitor;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoException;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoClassNotFoundException;
import org.picocontainer.alternatives.AbstractDelegatingMutablePicoContainer;
import org.picocontainer.adapters.CachingComponentAdapterFactory;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.defaults.ComponentMonitorStrategy;
import org.picocontainer.adapters.AnyInjectionComponentAdapterFactory;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.LifecycleStrategy;
import org.picocontainer.defaults.CustomPermissionsURLClassLoader;

/**
 * This is a MutablePicoContainer that also supports soft composition. i.e. assembly by class name rather that class
 * reference.
 * <p>
 * In terms of implementation it adopts the behaviour of DefaultPicoContainer and DefaultNanoContainer.</p>
 *
 * @author Paul Hammant
 * @author Mauro Talevi
 * @author Michael Rimov
 * @version $Revision$
 */
public class DefaultNanoContainer extends AbstractDelegatingMutablePicoContainer implements NanoContainer, Serializable,
    ComponentMonitorStrategy {

    private static transient Map<String, String> primitiveNameToBoxedName = new HashMap<String, String>();
    static {
        primitiveNameToBoxedName.put("int", Integer.class.getName());
        primitiveNameToBoxedName.put("byte", Byte.class.getName());
        primitiveNameToBoxedName.put("short", Short.class.getName());
        primitiveNameToBoxedName.put("long", Long.class.getName());
        primitiveNameToBoxedName.put("float", Float.class.getName());
        primitiveNameToBoxedName.put("double", Double.class.getName());
        primitiveNameToBoxedName.put("boolean", Boolean.class.getName());
    }


    private transient List<ClassPathElement> classPathElements = new ArrayList<ClassPathElement>();
    private transient ClassLoader parentClassLoader;

    private transient ClassLoader componentClassLoader;
    private transient boolean componentClassLoaderLocked;



    protected Map<String,PicoContainer> namedChildContainers = new HashMap<String,PicoContainer>();


    public DefaultNanoContainer(ClassLoader classLoader, ComponentAdapterFactory caf, PicoContainer parent) {
        super(new DefaultPicoContainer(caf, parent));
        parentClassLoader = classLoader;
    }

    public DefaultNanoContainer(ClassLoader classLoader, MutablePicoContainer delegate) {
        super(delegate);
        parentClassLoader = classLoader;

    }

    public DefaultNanoContainer(ClassLoader classLoader, PicoContainer parent, ComponentMonitor componentMonitor) {
        super(new DefaultPicoContainer(new CachingComponentAdapterFactory().forThis(new AnyInjectionComponentAdapterFactory()), parent));
        parentClassLoader = classLoader;
        ((ComponentMonitorStrategy)getDelegate()).changeMonitor(componentMonitor);
    }

    public DefaultNanoContainer(ComponentAdapterFactory caf) {
        super(new DefaultPicoContainer(caf, null));
        parentClassLoader = DefaultNanoContainer.class.getClassLoader();
    }

    public DefaultNanoContainer(PicoContainer parent) {
        super(new DefaultPicoContainer(parent));
        parentClassLoader = DefaultNanoContainer.class.getClassLoader();
    }

    public DefaultNanoContainer(MutablePicoContainer pico) {
        super(pico);
        parentClassLoader = DefaultNanoContainer.class.getClassLoader();
    }

    public DefaultNanoContainer(ClassLoader classLoader) {
        super(new DefaultPicoContainer());
        parentClassLoader = classLoader;
    }

    public DefaultNanoContainer() {
        super(new DefaultPicoContainer());
        parentClassLoader = DefaultNanoContainer.class.getClassLoader();
    }


    /**
     * Constructor that provides the same control over the nanocontainer lifecycle strategies
     * as {@link DefaultPicoContainer(ComponentAdapterFactory, LifecycleStrategy, PicoContainer)}.
     * @param componentAdapterFactory ComponentAdapterFactory
     * @param lifecycleStrategy LifecycleStrategy
     * @param parent PicoContainer may be null if there is no parent.
     * @param cl the Classloader to use.  May be null, in which case DefaultNanoPicoContainer.class.getClassLoader()
     * will be called instead.
     */
    public DefaultNanoContainer(ComponentAdapterFactory componentAdapterFactory,
        LifecycleStrategy lifecycleStrategy, PicoContainer parent, ClassLoader cl) {

        super(new DefaultPicoContainer(componentAdapterFactory, lifecycleStrategy, parent));
        parentClassLoader = (cl != null) ? cl : DefaultNanoContainer.class.getClassLoader();
    }


    protected DefaultNanoContainer createChildContainer() {
        MutablePicoContainer child = getDelegate().makeChildContainer();
        DefaultNanoContainer container = new DefaultNanoContainer(getComponentClassLoader(), child);
        container.changeMonitor(currentMonitor());
        return container;
     }

    public void changeMonitor(ComponentMonitor monitor) {
        ((ComponentMonitorStrategy)getDelegate()).changeMonitor(monitor);
    }

    public ComponentMonitor currentMonitor() {
        return ((ComponentMonitorStrategy)getDelegate()).currentMonitor();
    }

    public final Object getComponent(Object componentKeyOrType) throws PicoException {

        if (componentKeyOrType instanceof ClassName) {
            componentKeyOrType = loadClass(((ClassName) componentKeyOrType).className);
        }

        Object instance = getDelegate().getComponent(componentKeyOrType);

        if (instance != null) {
            return instance;
        }

        ComponentAdapter componentAdapter = null;
        if (componentKeyOrType.toString().startsWith("*")) {
            String candidateClassName = componentKeyOrType.toString().substring(1);
            Collection<ComponentAdapter> cas = getComponentAdapters();
            for (ComponentAdapter ca : cas) {
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
        DefaultNanoContainer child = createChildContainer();
        MutablePicoContainer parentDelegate = getDelegate();
        parentDelegate.removeChildContainer(child.getDelegate());
        parentDelegate.addChildContainer(child);
        namedChildContainers.put(name, child);
        return child;
    }

    public boolean removeChildContainer(PicoContainer child) {
        boolean result = getDelegate().removeChildContainer(child);
        Iterator<Map.Entry<String,PicoContainer>> children = namedChildContainers.entrySet().iterator();
        while (children.hasNext()) {
            Map.Entry<String,PicoContainer> e = children.next();
            PicoContainer pc = e.getValue();
            if (pc == child) {
                children.remove();
            }
        }
        return result;
    }

    protected final Map getNamedContainers() {
        return namedChildContainers;
    }


    public ClassPathElement addClassLoaderURL(URL url) {
        if (componentClassLoaderLocked) throw new IllegalStateException("ClassLoader URLs cannot be added once this instance is locked");

        ClassPathElement classPathElement = new ClassPathElement(url);
        classPathElements.add(classPathElement);
        return classPathElement;
    }

    public MutablePicoContainer addComponent(Object implOrInstance) {
        if(implOrInstance instanceof ClassName) {
            String className = ((ClassName) implOrInstance).className;
            return super.addComponent(loadClass(className));
        }
        return super.addComponent(implOrInstance);
    }

    public MutablePicoContainer addComponent(Object key, Object componentImplementationOrInstance, Parameter... parameters) {
        if (key instanceof ClassName) {
            key = loadClass(((ClassName) key).getClassName());
        }
        if (componentImplementationOrInstance instanceof ClassName) {
            componentImplementationOrInstance = loadClass(((ClassName) componentImplementationOrInstance).getClassName());
        }
        return super.addComponent(key,componentImplementationOrInstance, parameters);
    }

    public ClassLoader getComponentClassLoader() {
        if (componentClassLoader == null) {
            componentClassLoaderLocked = true;
            componentClassLoader = (ClassLoader) AccessController.doPrivileged(new PrivilegedAction() {
                public Object run() {
                    return new CustomPermissionsURLClassLoader(getURLs(classPathElements), makePermissions(), parentClassLoader);
                }
            });
        }
        return componentClassLoader;
    }

    public MutablePicoContainer addChildContainer(PicoContainer child) {
        getDelegate().addChildContainer(child);
        namedChildContainers.put("containers" + namedChildContainers.size(), child);
        return this;
    }

    public void addChildContainer(String name, PicoContainer child) {

        super.addChildContainer(child);

        namedChildContainers.put(name, child);
    }

    private Class loadClass(final String className) {
        ClassLoader classLoader = getComponentClassLoader();
        String cn = getClassName(className);
        try {
            return classLoader.loadClass(cn);
        } catch (ClassNotFoundException e) {
            throw new PicoClassNotFoundException(cn, e);
        }
    }

    private Map<URL, PermissionCollection> makePermissions() {
        Map<URL, PermissionCollection> permissionsMap = new HashMap<URL, PermissionCollection>();
        for (ClassPathElement cpe : classPathElements) {
            PermissionCollection permissionCollection = cpe.getPermissionCollection();
            permissionsMap.put(cpe.getUrl(), permissionCollection);
        }
        return permissionsMap;
    }

    private URL[] getURLs(List<ClassPathElement> classPathElemelements) {
        final URL[] urls = new URL[classPathElemelements.size()];
        for(int i = 0; i < urls.length; i++) {
            urls[i] = (classPathElemelements.get(i)).getUrl();
        }
        return urls;
    }

    private static String getClassName(String primitiveOrClass) {
        String fromMap = primitiveNameToBoxedName.get(primitiveOrClass);
        return fromMap != null ? fromMap : primitiveOrClass;
    }



}