package org.picocontainer.gui.tree;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class ComponentTreeNode extends DefaultMutableTreeNode {
    public ComponentTreeNode(Class componentImplementation) {
        super(componentImplementation);
    }
}
