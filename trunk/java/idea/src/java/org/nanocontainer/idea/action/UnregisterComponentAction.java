package org.picoextras.idea.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.picocontainer.PicoContainer;
import org.picoextras.swing.IconHelper;

import javax.swing.*;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class UnregisterComponentAction extends AnAction {
    private PicoContainer picoContainer;

    public UnregisterComponentAction(PicoContainer picoContainer) {
        super("Unregister Component", "Unregister Component", IconHelper.getIcon("/actions/exclude.png"));
        this.picoContainer = picoContainer;
    }

    public void actionPerformed(AnActionEvent event) {
        JOptionPane.showMessageDialog(null, "Unregister component not yet implemented");
    }
}
