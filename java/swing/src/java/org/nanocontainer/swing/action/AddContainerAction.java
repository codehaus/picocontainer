/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
package org.nanocontainer.swing.action;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.nanocontainer.swing.ContainerTree;

import java.awt.event.ActionEvent;
import java.util.Map;
import java.util.HashMap;

public class AddContainerAction extends TreeSelectionAction {
    private int i;

    public AddContainerAction(String iconPath, ContainerTree tree) {
        super("Add Container", iconPath, tree);
    }

    public void actionPerformed(ActionEvent evt) {
        PicoContainer pico = new DefaultPicoContainer(selectedContainer);
        ComponentAdapter ca = selectedContainer.registerComponentInstance("" + i++, pico);
        containerTreeModel.fire(ca);
    }

    protected void setEnabled() {
        setEnabled(selectedContainer != null);
    }
}