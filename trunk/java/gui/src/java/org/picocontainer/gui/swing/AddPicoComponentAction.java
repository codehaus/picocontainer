package org.picocontainer.gui.swing;

import org.picocontainer.gui.model.*;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import java.awt.event.ActionEvent;
import java.awt.*;

/**
 * Action that adds a new component. Enables itself if a container node
 * is selected and the current classname is a loadabe class. Otherwise
 * disabled.
 *
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class AddPicoComponentAction extends AbstractAction implements TreeSelectionListener, DocumentListener {
    private final ComponentRegistrar componentRegistrar;
    private final Component errorDialogParent;

    private ContainerNode selectedContainerNode = null;
    private Class componentImplementation = null;

    public AddPicoComponentAction(
            ComponentRegistrar componentRegistrar, 
            Component errorDialogParent,
            JTree treeToListenTo,
            Document documentToListenTo) {
        super("Add PicoComponent");
        this.componentRegistrar = componentRegistrar;
        this.errorDialogParent = errorDialogParent;

        treeToListenTo.addTreeSelectionListener(this);
        documentToListenTo.addDocumentListener(this);

        setEnabled(false);
    }

    public void actionPerformed(ActionEvent evt) {
        try {
            componentRegistrar.createComponentNodeInTree(selectedContainerNode, componentImplementation, componentImplementation);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(errorDialogParent,e.getStackTrace(),e.getClass().getName() + " " + e.getMessage(), JOptionPane.ERROR_MESSAGE);
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

    public void insertUpdate(DocumentEvent e) {
        setClassName(e);
    }

    public void removeUpdate(DocumentEvent e) {
        setClassName(e);
    }

    public void changedUpdate(DocumentEvent e) {
        setClassName(e);
    }

    private void setClassName(DocumentEvent evt) {
        try {
            String currentClassName = evt.getDocument().getText(0, evt.getDocument().getLength());
            try {
                componentImplementation = getClass().getClassLoader().loadClass(currentClassName);
            } catch (ClassNotFoundException e) {
                componentImplementation = null;
            }
            setEnabled();
        } catch (BadLocationException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    private void setEnabled() {
        setEnabled(componentImplementation != null && selectedContainerNode != null);
    }
}