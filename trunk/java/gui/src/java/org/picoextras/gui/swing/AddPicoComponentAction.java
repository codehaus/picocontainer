package org.picoextras.gui.swing;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
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
public class AddPicoComponentAction extends AddToContainerNodeAction implements DocumentListener {
    private final ComponentRegistrar componentRegistrar;

    private Class componentImplementation = null;

    public AddPicoComponentAction(
            ComponentRegistrar componentRegistrar, 
            Component errorDialogParent,
            JTree treeToListenTo,
            Document documentToListenTo) {
        super("Add PicoComponent", errorDialogParent, treeToListenTo);
        this.componentRegistrar = componentRegistrar;

        documentToListenTo.addDocumentListener(this);
    }

    public void actionPerformed(ActionEvent evt) {
        try {
            componentRegistrar.createComponentNodeInTree(getSelectedContainerNode(), componentImplementation, componentImplementation);
        } catch (Exception e) {
            showErrorDialog(e);
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

    protected void setEnabled() {
        setEnabled(componentImplementation != null && getSelectedContainerNode() != null);
    }
}