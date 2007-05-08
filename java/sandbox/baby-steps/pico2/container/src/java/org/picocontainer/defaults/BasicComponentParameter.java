/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.defaults;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoVisitor;
import org.picocontainer.ParameterName;

/**
 * A BasicComponentParameter should be used to pass in a particular addComponent as argument to a
 * different addComponent's constructor. This is particularly useful in cases where several
 * components of the same type have been registered, but with a different key. Passing a
 * ComponentParameter as a parameter when registering a addComponent will give PicoContainer a hint
 * about what other addComponent to use in the constructor. This Parameter will never resolve
 * against a collecting type, that is not directly registered in the PicoContainer itself.
 *
 * @author Jon Tirs&eacute;n
 * @author Aslak Helles&oslash;y
 * @author J&ouml;rg Schaible
 * @author Thomas Heller
 * @version $Revision$
 */
public class BasicComponentParameter
        implements Parameter, Serializable {

    /**
     * <code>BASIC_DEFAULT</code> is an instance of BasicComponentParameter using the default constructor.
     */
    public static final BasicComponentParameter BASIC_DEFAULT = new BasicComponentParameter();

    private Object componentKey;

    /**
     * Expect a parameter matching a addComponent of a specific key.
     *
     * @param componentKey the key of the desired addComponent
     */
    public BasicComponentParameter(Object componentKey) {
        this.componentKey = componentKey;
    }

    /**
     * Expect any paramter of the appropriate type.
     */
    public BasicComponentParameter() {
    }

    /**
     * Check wether the given Parameter can be statisfied by the container.
     *
     * @return <code>true</code> if the Parameter can be verified.
     * @throws org.picocontainer.PicoInitializationException {@inheritDoc}
     * @see org.picocontainer.Parameter#isResolvable(org.picocontainer.PicoContainer,org.picocontainer.ComponentAdapter,Class,org.picocontainer.ParameterName)
     */
    public boolean isResolvable(PicoContainer container, ComponentAdapter adapter, Class expectedType, ParameterName expectedParameterName) {
        return resolveAdapter(container, adapter, expectedType, expectedParameterName) != null;
    }

    public Object resolveInstance(PicoContainer container, ComponentAdapter adapter, Class expectedType, ParameterName expectedParameterName) {
        final ComponentAdapter componentAdapter = resolveAdapter(container, adapter, expectedType, expectedParameterName);
        if (componentAdapter != null) {
            return container.getComponent(componentAdapter.getComponentKey());
        }
        return null;
    }

    public void verify(PicoContainer container, ComponentAdapter adapter, Class expectedType, ParameterName expectedParameterName) {
        final ComponentAdapter componentAdapter = resolveAdapter(container, adapter, expectedType, expectedParameterName);
        if (componentAdapter == null) {
            final HashSet set = new HashSet();
            set.add(expectedType);
            throw new UnsatisfiableDependenciesException(adapter, set, container);
        }
        componentAdapter.verify(container);
    }

    /**
     * Visit the current {@link Parameter}.
     *
     * @see org.picocontainer.Parameter#accept(org.picocontainer.PicoVisitor)
     */
    public void accept(final PicoVisitor visitor) {
        visitor.visitParameter(this);
    }

    private ComponentAdapter resolveAdapter(PicoContainer container, ComponentAdapter adapter, Class expectedType, ParameterName expectedParameterName) {

        final ComponentAdapter result = getTargetAdapter(container, expectedType, expectedParameterName, adapter);
        if (result == null) {
            return null;
        }

        if (!expectedType.isAssignableFrom(result.getComponentImplementation())) {
            // check for primitive value
            if (expectedType.isPrimitive()) {
                try {
                    final Field field = result.getComponentImplementation().getField("TYPE");
                    final Class type = (Class) field.get(result.getComponentInstance(null));
                    if (expectedType.isAssignableFrom(type)) {
                        return result;
                    }
                } catch (NoSuchFieldException e) {
                } catch (IllegalArgumentException e) {
                } catch (IllegalAccessException e) {
                } catch (ClassCastException e) {
                }
            }
            return null;
        }
        return result;
    }

    private ComponentAdapter getTargetAdapter(PicoContainer container, Class expectedType, ParameterName expectedParameterName, ComponentAdapter excludeAdapter) {
        if (componentKey != null) {
            // key tells us where to look so we follow
            return container.getComponentAdapter(componentKey);
        } else if(excludeAdapter == null) {
            return container.getComponentAdapter(expectedType);
        } else {
            Object excludeKey = excludeAdapter.getComponentKey();
            ComponentAdapter byKey = container.getComponentAdapter((Object)expectedType);
            if(byKey != null && !excludeKey.equals(byKey.getComponentKey())) {
                return byKey;
            }
            List<ComponentAdapter> found = container.getComponentAdapters(expectedType);
            ComponentAdapter exclude = null;
            for (ComponentAdapter work : found) {
                if (work.getComponentKey().equals(excludeKey)) {
                    exclude = work;
                }
            }
            found.remove(exclude);
            if(found.size() == 0) {
                if( container.getParent() != null) {
                    return container.getParent().getComponentAdapter(expectedType);
                } else {
                    return null;
                }
            } else if(found.size() == 1) {
                return found.get(0);
            } else {
                for (ComponentAdapter componentAdapter : found) {
                    Object key = componentAdapter.getComponentKey();
                    if (key instanceof String && key.equals(expectedParameterName.getParameterName())) {
                        return componentAdapter;
                    }
                }

                Class[] foundClasses = new Class[found.size()];
                for (int i = 0; i < foundClasses.length; i++) {
                    foundClasses[i] = found.get(i).getComponentImplementation();
                }
                throw new AmbiguousComponentResolutionException(expectedType, foundClasses);
            }
        }
    }
}
