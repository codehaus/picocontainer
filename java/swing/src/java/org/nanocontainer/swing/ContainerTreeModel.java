package org.picoextras.swing;

import org.picocontainer.PicoContainer;
import org.picoextras.guimodel.ContainerModel;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * A tree model based on a PicoContainer hierarchy.
 * 
 * @author Laurent Etiemble
 * @version $Revision$
 */
public class ContainerTreeModel extends ContainerModel implements TreeModel {
    public ContainerTreeModel(PicoContainer pico) {
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
        // Do nothing
    }

    public void removeTreeModelListener(TreeModelListener l) {
        // Do nothing
    }

    public Object getChild(Object parent, int index) {
        return getChildAt(parent, index);
    }

    public void valueForPathChanged(TreePath path, Object newValue) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
