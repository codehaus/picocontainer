/*
 * Created by IntelliJ IDEA.
 * User: ahelleso
 * Date: 30-Jan-2004
 * Time: 01:51:26
 */
package org.picoextras.swing.action;

import org.picoextras.swing.ContainerTree;

import java.awt.event.ActionEvent;

public class StopContainerAction extends TreeSelectionAction {
    public StopContainerAction(String iconPath, ContainerTree tree) {
        super("Stop Container", iconPath, tree);
    }

    public void actionPerformed(ActionEvent e) {

    }

    protected void setEnabled() {

    }
}