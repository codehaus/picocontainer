package org.picocontainer.gui.swing;

import org.picocontainer.gui.model.ContainerNode;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.*;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class AddPicoContainerAction extends AddToContainerNodeAction {
    private final TreeModel treeModel;

    public AddPicoContainerAction(
            Component errorDialogParent,
            TreeModel treeModel,
            JTree treeToListenTo) {
        super("Add PicoContainer", errorDialogParent, treeToListenTo);
        this.treeModel = treeModel;
    }

    protected void setEnabled() {
        setEnabled(getSelectedContainerNode() != null);

    }

    public void actionPerformed(ActionEvent e) {
        ContainerNode containerNode = new ContainerNode();
//        treeModel.insertNodeInto(containerNode, getSelectedContainerNode(), 0);
    }
}
