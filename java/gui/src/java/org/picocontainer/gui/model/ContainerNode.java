package org.picocontainer.gui.model;

import org.picocontainer.*;
import org.picocontainer.extras.DelegatingPicoContainer;
import org.picocontainer.defaults.*;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

/**
 * Usage pattern:
 * <p>
 * ContainerNode newChild = root.createRegistryChild(new DefaultComponentRegistry());
 * model.insertNodeInto(root, newChild, 0);
 *
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class ContainerNode extends DefaultMutableTreeNode {
    // These never instantiate components.
    private DefaultPicoContainer tempPico = new DefaultPicoContainer();

    public ContainerNode() {
        super("DUMMY");
    }


    public MutablePicoContainer createPicoContainer() throws PicoInitializationException {
        MutablePicoContainer result;
        if(getParent() != null) {
            ContainerNode parent = (ContainerNode) getParent();
            MutablePicoContainer parentContainer = parent.createPicoContainer();
            result = new DelegatingPicoContainer(parentContainer);
        } else {
            result = new DefaultPicoContainer();
        }

        int childCount = getChildCount();
        for(int i = 0; i < childCount; i++) {
            TreeNode child = getChildAt(i);
            if(child instanceof ComponentNode) {
                ComponentNode componentNode = (ComponentNode) child;
                Class componentImplementation = (Class) componentNode.getUserObject();
                result.registerComponentImplementation(componentImplementation);
            }
        }
        return result;
    }
}
