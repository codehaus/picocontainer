package org.picocontainer.containers;


import org.picocontainer.PicoContainer;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoVisitor;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.adapters.InstanceAdapter;

import java.io.Serializable;
import java.util.List;
import java.util.Collection;

public class ArgumentativePicoContainer implements PicoContainer, Serializable {

    private MutablePicoContainer delegate = new DefaultPicoContainer();

    public ArgumentativePicoContainer(String[] arguments) {
        this("=", arguments);
    }

    public ArgumentativePicoContainer(String separater, String[] arguments) {
        for (String argument : arguments) {
            String[] kvs = argument.split(separater);
            if (kvs.length == 2) {
                delegate.addComponent(kvs[0], getValue(kvs[1]));
            } else if (kvs.length == 1) {
                delegate.addComponent(kvs[0], true);
            } else if (kvs.length > 2) {
                throw new PicoCompositionException(
                    "Argument name=value pair '" + argument + "' has too many '=' characters");
            }
        }
    }

    private Object getValue(String s) {
        if (s.equals("true")) {
            return true;
        } else if (s.equals("false")) {
            return false;
        }
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
        }
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
        }

        return s;
    }

    public Object getComponent(Object componentKeyOrType) {
        return delegate.getComponent(componentKeyOrType);
    }

    public <T> T getComponent(Class<T> componentType) {
        return delegate.getComponent(componentType);
    }

    public List getComponents() {
        return delegate.getComponents();
    }

    public PicoContainer getParent() {
        return delegate.getParent();
    }

    public ComponentAdapter<?> getComponentAdapter(Object componentKey) {
        return delegate.getComponentAdapter(componentKey);
    }

    public <T> ComponentAdapter<T> getComponentAdapter(Class<T> componentType) {
        return delegate.getComponentAdapter(componentType);
    }

    public Collection<ComponentAdapter<?>> getComponentAdapters() {
        return delegate.getComponentAdapters();
    }

    public <T> List<ComponentAdapter<T>> getComponentAdapters(Class<T> componentType) {
        return delegate.getComponentAdapters(componentType);
    }

    public <T> List<T> getComponents(Class<T> componentType) {
        return delegate.getComponents(componentType);
    }

    public void accept(PicoVisitor visitor) {
        delegate.accept(visitor);

    }
}
