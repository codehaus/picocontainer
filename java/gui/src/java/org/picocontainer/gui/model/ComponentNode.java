package org.picocontainer.gui.model;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class ComponentNode extends DefaultMutableTreeNode {
    public ComponentNode(BeanPropertyModel beanPropertyModel) {
        super(beanPropertyModel);
    }
}
