/*
 * Created by IntelliJ IDEA.
 * User: ahelleso
 * Date: 30-Jan-2004
 * Time: 01:51:26
 */
package org.nanocontainer.swing.action;

import org.nanocontainer.swing.ContainerTree;

import java.awt.event.ActionEvent;

public class StartContainerAction extends TreeSelectionAction {
    public StartContainerAction(String iconPath, ContainerTree tree) {
        super("Start Container", iconPath, tree);
    }

    public void actionPerformed(ActionEvent e) {

    }

    protected void setEnabled() {

    }
}