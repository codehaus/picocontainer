package org.nanocontainer.guimodel;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.DecoratingComponentAdapter;
import org.picocontainer.defaults.InstanceComponentAdapter;

import java.util.Collection;
import java.util.Iterator;

/**
 * An abstract class that is used to access the content of a PicoContainer.
 *
 * @author Laurent Etiemble
 * @version $Revision$
 */
public abstract class ContainerModel {
    private final MutablePicoContainer pico;
    private boolean expandAdapters = false;

    protected ContainerModel(MutablePicoContainer pico) {
        this.pico = pico;
    }

    protected PicoContainer getRootContainer() {
        return pico;
    }

    protected Object[] getAllChildren(Object node) {
        Object[] result = new Object[0];
        if (node instanceof PicoContainer) {
            PicoContainer pc = (PicoContainer) node;
            result = getAllChildren(pc);
        }
        if (node instanceof ComponentAdapter) {
            ComponentAdapter componentAdapter = (ComponentAdapter) node;
            result = getAllChildren(componentAdapter);
        }
        return result;
    }

    protected int getChildIndex(Object parent, Object child) {
        int index = -1;
        Object[] children = getAllChildren(parent);
        for (int i = 0; i < children.length; i++) {
            if (children[i].equals(child)) {
                index = i;
                break;
            }
        }
        return index;
    }

    protected Object getChildAt(Object parent, int index) {
        return getAllChildren(parent)[index];
    }

    private Object[] getAllChildren(PicoContainer parent) {
        Collection componentAdapters = parent.getComponentAdapters();
        Object[] result = new Object[componentAdapters.size()];
        int i = 0;
        for (Iterator it = componentAdapters.iterator(); it.hasNext();) {
            ComponentAdapter componentAdapter = (ComponentAdapter) it.next();
            if(componentAdapter instanceof InstanceComponentAdapter) {
                InstanceComponentAdapter instanceComponentAdapter = (InstanceComponentAdapter) componentAdapter;
                result[i++] = instanceComponentAdapter.getComponentInstance();
            } else {
                result[i++] = componentAdapter;
            }
        }
        return result;
    }

    private Object[] getAllChildren(ComponentAdapter parent) {
        if (expandAdapters) {
            if (parent instanceof DecoratingComponentAdapter) {
                DecoratingComponentAdapter dca = (DecoratingComponentAdapter) parent;
                return new Object[]{dca.getDelegate()};
            } else {
                return new Object[]{parent.getComponentImplementation()};
            }
        } else {
            return new Object[0];
        }
    }
}
