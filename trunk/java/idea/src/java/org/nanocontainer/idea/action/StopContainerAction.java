package org.picoextras.idea.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.picoextras.swing.IconHelper;
import org.picocontainer.PicoContainer;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class StopContainerAction extends AnAction {
    private PicoContainer picoContainer;

    public StopContainerAction(PicoContainer picoContainer) {
        super("Stop Container", "Stop Container", IconHelper.getIcon("/actions/suspend.png"));
        this.picoContainer = picoContainer;
    }

    public void actionPerformed(AnActionEvent event) {
        picoContainer.stop();
    }
}
