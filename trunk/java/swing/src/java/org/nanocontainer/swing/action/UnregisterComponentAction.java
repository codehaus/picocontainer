/*
 * Created by IntelliJ IDEA.
 * User: ahelleso
 * Date: 30-Jan-2004
 * Time: 01:51:26
 */
package org.nanocontainer.swing.action;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.ComponentAdapter;
import org.nanocontainer.swing.ContainerTree;

import java.awt.event.ActionEvent;

public class UnregisterComponentAction extends TreeSelectionAction {
    public UnregisterComponentAction(String iconPath, ContainerTree tree) {
        super("Unregister Component", iconPath, tree);
    }

    public void actionPerformed(ActionEvent e) {
        MutablePicoContainer parent = null;
        Object selectedKey = null;
        ComponentAdapter removed;
        if(selectedContainer != null) {
            parent = (MutablePicoContainer) selectedContainer.getParent();
            removed = parent.unregisterComponentByInstance(selectedContainer);
        } else {
            parent = (MutablePicoContainer) selectedAdapter.getContainer();
            selectedKey = selectedAdapter.getComponentKey();
            removed = parent.unregisterComponent(selectedKey);
        }
        containerTreeModel.fire(removed);
    }

    protected void setEnabled() {
        setEnabled(selected != containerTreeModel.getRoot() && selected != null);
    }
}