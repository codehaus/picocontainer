package org.picocontainer.gui.swing;

import org.picocontainer.PicoContainer;

import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.event.TreeModelListener;
import java.util.List;
import java.util.ArrayList;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class PicoTreeModel implements TreeModel {
    private final PicoContainer picoContainer;

    public PicoTreeModel(PicoContainer picoContainer) {
        this.picoContainer = picoContainer;
    }
    
    // TreeModel interface
    
    public Object getRoot() {
        return picoContainer;
    }

    public int getChildCount(Object parent) {
        if (parent instanceof PicoContainer) {
            PicoContainer pico = (PicoContainer) parent;
            return pico.getChildContainers().size() + picoContainer.getComponentKeys().size();
        } else {
            return 0;
        }
    }

    public boolean isLeaf(Object node) {
        return !(node instanceof PicoContainer);
    }

    public void addTreeModelListener(TreeModelListener l) {

    }

    public void removeTreeModelListener(TreeModelListener l) {

    }

    public Object getChild(Object parent, int index) {
        PicoContainer pico = (PicoContainer) parent;
        List children = new ArrayList(pico.getChildContainers());
        List componentKeys = new ArrayList(pico.getComponentKeys());

        if (index < children.size()) {
            return children.get(index);
        } else {
            Object componentKey = componentKeys.get(index - children.size());
            return pico.findComponentAdapter(componentKey);
        }
    }

    public int getIndexOfChild(Object parent, Object child) {
        PicoContainer pico = (PicoContainer) parent;
        List children = new ArrayList(pico.getChildContainers());
        List componentKeys = new ArrayList(pico.getComponentKeys());

        if (child instanceof PicoContainer) {
            return children.indexOf(child);
        } else {
            return componentKeys.indexOf(child) + children.size();
        }
    }

    public void valueForPathChanged(TreePath path, Object newValue) {

    }
}
