package org.picocontainer.gui.model;

import org.picocontainer.*;
import org.picocontainer.extras.DecoratingPicoContainer;
import org.picocontainer.defaults.*;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import java.util.Iterator;

/**
 * Usage pattern:
 * <p>
 * ComponentRegistryTreeNode newChild = root.createRegistryChild(new DefaultComponentRegistry());
 * model.insertNodeInto(root, newChild, 0);
 *
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class ComponentRegistryTreeNode extends DefaultMutableTreeNode {
    // These never instantiate components.
    private DefaultPicoContainer tempPico = new DefaultPicoContainer();

    public ComponentRegistryTreeNode() {
        super("DUMMY");
    }

    public void insert(MutableTreeNode newChild, int i) {
        if (newChild instanceof ComponentTreeNode) {
            ComponentTreeNode componentTreeNode = (ComponentTreeNode) newChild;
            ComponentAdapter componentAdapter = (ComponentAdapter) componentTreeNode.getUserObject();
            tempPico.registerComponent(componentAdapter);
        }
        super.insert(newChild, i);
    }

    public void remove(MutableTreeNode aChild) {
        if (aChild instanceof ComponentTreeNode) {
            ComponentTreeNode componentTreeNode = (ComponentTreeNode) aChild;
            ComponentAdapter componentAdapter = (ComponentAdapter) componentTreeNode.getUserObject();
            tempPico.unregisterComponent(componentAdapter.getComponentKey());
        }

        super.remove(aChild);
    }

    public DecoratingPicoContainer createHierarchicalComponentRegistry() throws PicoInitializationException {

        DefaultPicoContainer childPico = new DefaultPicoContainer();

        for (Iterator iterator = tempPico.getComponentAdapters().iterator(); iterator.hasNext();) {
            ComponentAdapter adapter = (ComponentAdapter) iterator.next();
            // todo hmmm. can we reuse it??
            childPico.registerComponent(adapter);
        }

        ComponentRegistryTreeNode parentNode = (ComponentRegistryTreeNode) getParent();
        AbstractPicoContainer parentPico = null;
        if (parentNode != null) {
            parentPico = parentNode.createHierarchicalComponentRegistry();
        } else {
            parentPico = new DefaultPicoContainer();
        }
        DecoratingPicoContainer result = new DecoratingPicoContainer();
        result.addDelegate(childPico);
        result.addDelegate(parentPico);
        return result;
    }
}
