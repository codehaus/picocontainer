package org.picoextras.gui.swing;

import org.picoextras.gui.model.ContainerNode;

import javax.swing.*;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import java.awt.*;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public abstract class AddToContainerNodeAction extends AbstractAction implements TreeSelectionListener {
    private final Component errorDialogParent;
    private ContainerNode selectedContainerNode = null;

    public AddToContainerNodeAction(
            String name,
            Component errorDialogParent,
            JTree treeToListenTo) {
        super(name);
        this.errorDialogParent = errorDialogParent;
        treeToListenTo.addTreeSelectionListener(this);
        setEnabled();
    }

    public void valueChanged(TreeSelectionEvent evt) {
        Object selected = evt.getPath().getLastPathComponent();
        if(selected instanceof ContainerNode) {
            selectedContainerNode = (ContainerNode) selected;
        } else {
            selectedContainerNode = null;
        }
        setEnabled();
    }

    protected ContainerNode getSelectedContainerNode() {
        return selectedContainerNode;
    }

    protected void showErrorDialog(Exception e) {
        JOptionPane.showMessageDialog(errorDialogParent,e.getStackTrace(),e.getClass().getName() + " " + e.getMessage(), JOptionPane.ERROR_MESSAGE);
    }

    protected abstract void setEnabled();
}
