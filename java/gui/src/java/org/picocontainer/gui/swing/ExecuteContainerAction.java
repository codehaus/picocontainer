package org.picocontainer.gui.swing;

import org.picocontainer.gui.model.*;
import org.picocontainer.MutablePicoContainer;

import javax.swing.*;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import java.awt.event.ActionEvent;

/**
 * Action that adds a new component. Enables itself if a container node
 * is selected and the current classname is a loadabe class. Otherwise
 * disabled.
 *
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class ExecuteContainerAction extends AbstractAction implements TreeSelectionListener {
    private ContainerNode selectedContainerNode = null;
    private JTree treeToListenTo;

    public ExecuteContainerAction(JTree treeToListenTo) {
        super("Execute PicoContainer");
        this.treeToListenTo = treeToListenTo;
        treeToListenTo.addTreeSelectionListener(this);
        setEnabled();
    }

    public void actionPerformed(ActionEvent evt) {
        try {
            // will call execute() on the components. Cuz we have such a smart,
            // chained componentadapter
            MutablePicoContainer picoContainer = selectedContainerNode.createPicoContainer();
            picoContainer.getComponentInstances();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(treeToListenTo,e.getStackTrace(),e.getClass().getName() + " " + e.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
    }

    public void valueChanged(TreeSelectionEvent evt) {
        Object selected = evt.getPath().getLastPathComponent();
        if(selected instanceof ContainerNode) {
            selectedContainerNode = (ContainerNode) selected;
            setEnabled();
        } else {
            selectedContainerNode = null;
            setEnabled();
        }
    }

    private void setEnabled() {
        setEnabled(selectedContainerNode != null && selectedContainerNode.getChildCount() > 0);
    }
}