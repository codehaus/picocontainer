package org.picoextras.idea.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.picoextras.swing.IconHelper;
import org.picocontainer.PicoContainer;

import javax.swing.*;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class RegisterComponentAction extends AnAction {
    private PicoContainer picoContainer;

    public RegisterComponentAction(PicoContainer picoContainer) {
        super("Register Component", "Register Component", IconHelper.getIcon("/nodes/entryPoints.png"));
        this.picoContainer = picoContainer;
    }

    public void actionPerformed(AnActionEvent event) {
        JOptionPane.showMessageDialog(null, "Register component not yet implemented");
    }
}
