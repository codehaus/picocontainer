package org.picoextras.idea.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.picoextras.swing.IconHelper;
import org.picocontainer.PicoContainer;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class StartContainerAction extends AnAction {
    private PicoContainer picoContainer;

    public StartContainerAction(PicoContainer picoContainer) {
        super("Start Container", "Start Container", IconHelper.getIcon("/general/toolWindowRun.png"));
        this.picoContainer = picoContainer;
    }

    public void actionPerformed(AnActionEvent event) {
        picoContainer.start();
    }
}
