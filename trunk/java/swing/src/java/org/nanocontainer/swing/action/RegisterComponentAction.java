/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
package org.picoextras.swing.action;

import org.picocontainer.ComponentAdapter;
import org.picoextras.swing.ContainerTree;

import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;

public class RegisterComponentAction extends TreeSelectionAction {
    private int i;

    public RegisterComponentAction(String iconPath, ContainerTree tree) {
        super("Register Component", iconPath, tree);
    }

    public void actionPerformed(ActionEvent evt) {
        String className = (String) JOptionPane.showInputDialog(null, "Component Implementation", "Register Component", JOptionPane.OK_CANCEL_OPTION, null, null, null);
        if (className != null) {
            try {
                Class componentImplementation = Class.forName(className);
                ComponentAdapter ca = selectedContainer.registerComponentImplementation("" + i++,  componentImplementation);
                containerTreeModel.fire(ca);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Component registration failed with " + e.getClass().getName() + ": " + e.getMessage(), null, JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    protected void setEnabled() {
        setEnabled(selectedContainer != null);
    }
}