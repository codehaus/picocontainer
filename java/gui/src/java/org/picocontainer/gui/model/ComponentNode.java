package org.picocontainer.gui.model;

import org.picocontainer.ComponentAdapter;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class ComponentNode extends DefaultMutableTreeNode {
    public ComponentNode(Class componentImplementation) {
        super(componentImplementation);
    }
}
