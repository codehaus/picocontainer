package org.nanocontainer.swing;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.nanocontainer.guimodel.ContainerModel;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.EventListenerList;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * A tree model based on a PicoContainer hierarchy.
 *
 * @author Laurent Etiemble
 * @version $Revision$
 */
public class ContainerTreeModel extends ContainerModel implements TreeModel {
    private EventListenerList listenerList = new EventListenerList();

    public ContainerTreeModel(MutablePicoContainer pico) {
        super(pico);
    }

    public Object getRoot() {
        return getRootContainer();
    }

    public int getChildCount(Object parent) {
        return getAllChildren(parent).length;
    }

    public boolean isLeaf(Object node) {
        return getChildCount(node) == 0;
    }

    public int getIndexOfChild(Object parent, Object child) {
        return getChildIndex(parent, child);
    }

    public void addTreeModelListener(TreeModelListener l) {
        listenerList.add(TreeModelListener.class, l);
    }

    public void removeTreeModelListener(TreeModelListener l) {
        listenerList.remove(TreeModelListener.class, l);
    }

    public Object getChild(Object parent, int index) {
        return getChildAt(parent, index);
    }

    public void valueForPathChanged(TreePath path, Object newValue) {
    }

    public void fire(ComponentAdapter componentAdapter) {
        PicoContainer[] path = getPathToRoot(componentAdapter);
        PicoContainer last = path[path.length - 1];
        Collection componentAdapters = last.getComponentAdapters();
        int[] indices = new int[componentAdapters.size()];
        ComponentAdapter[] children = new ComponentAdapter[componentAdapters.size()];
        int i = 0;
        for (Iterator iterator = componentAdapters.iterator(); iterator.hasNext();) {
            ComponentAdapter ca = (ComponentAdapter) iterator.next();
            children[i] = ca;
            indices[i] = i;
            i++;
        }
        TreeModelEvent evt = new TreeModelEvent(this, path, indices, children);

        Object[] listeners = listenerList.getListenerList();
        for (int j = listeners.length - 2; j >= 0; j -= 2) {
            if (listeners[j] == TreeModelListener.class) {
                ((TreeModelListener) listeners[j + 1]).treeStructureChanged(evt);
            }
        }
    }

    private PicoContainer[] getPathToRoot(ComponentAdapter componentAdapter) {
        List result = new ArrayList();
        PicoContainer parent = componentAdapter.getContainer();
        while (parent != null) {
            result.add(0, parent);
            parent = parent.getParent();
        }
        return (PicoContainer[]) result.toArray(new PicoContainer[result.size()]);
    }
}
