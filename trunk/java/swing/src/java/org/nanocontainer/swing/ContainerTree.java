package org.picoextras.swing;

import org.picocontainer.PicoContainer;

import javax.swing.*;

/**
 * Simple tree that takes a PicoContainer as root object.
 * 
 * @author Laurent Etiemble
 * @version $Revision$
 */
public class ContainerTree extends JTree {

	public ContainerTree(PicoContainer pico) {
		this(new ContainerTreeModel(pico));
	}

	public ContainerTree(ContainerTreeModel newModel) {
		super(newModel);
		this.setRootVisible(true);
		this.setCellRenderer(new ContainerTreeCellRenderer());
	}
}
