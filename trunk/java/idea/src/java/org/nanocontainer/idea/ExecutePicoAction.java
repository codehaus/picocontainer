package org.picoextras.idea;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

/**
 * Action that executes XDoclet.
 *
 * @author <a href="mailto:aslak.hellesoy at bekk.no">Aslak Helles&oslash;y</a>
 * @version $Revision$
 */
public class ExecutePicoAction extends AnAction {
    public void actionPerformed(AnActionEvent event) {
        System.out.println("Run Pico, Ruuuun!");
    }
}
