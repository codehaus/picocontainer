package org.picocontainer.gui.tree;

import org.picocontainer.extras.HierarchicalComponentRegistry;
import org.picocontainer.internals.ComponentRegistry;
import org.picocontainer.internals.ComponentAdapter;
import org.picocontainer.*;
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
    private ComponentRegistry tempRegistry = new DefaultComponentRegistry();
    private RegistrationPicoContainer tempContainer = new DefaultPicoContainer.WithComponentRegistry(tempRegistry);

    public ComponentRegistryTreeNode() {
        super("DUMMY");
    }

    public void insert(MutableTreeNode newChild, int i) {
        if (newChild instanceof ComponentTreeNode) {
            ComponentTreeNode componentTreeNode = (ComponentTreeNode) newChild;

            try {
                tempContainer.registerComponentByClass((Class) componentTreeNode.getUserObject());
            } catch (PicoException e) {
                throw new RuntimeException(e);
            }
        }
        super.insert(newChild, i);
    }

    public void remove(MutableTreeNode aChild) {
        if (aChild instanceof ComponentTreeNode) {
            ComponentTreeNode componentTreeNode = (ComponentTreeNode) aChild;
            tempContainer.unregisterComponent((Class) componentTreeNode.getUserObject());
        }

        super.remove(aChild);
    }

    public HierarchicalComponentRegistry createHierarchicalComponentRegistry() throws PicoInitializationException {

        ComponentRegistry componentRegistry = new DefaultComponentRegistry();
        RegistrationPicoContainer registrationOnlyContainer = new DefaultPicoContainer.WithComponentRegistry(componentRegistry);

        System.out.println("REG specs:" + tempRegistry.getComponentAdapters());


        for (Iterator iterator = tempRegistry.getComponentAdapters().iterator(); iterator.hasNext();) {
            ComponentAdapter adapter = (ComponentAdapter) iterator.next();
            try {
                registrationOnlyContainer.registerComponentByClass(adapter.getComponentImplementation());
            } catch (PicoException e) {
                throw new RuntimeException(e);
            }
        }

        ComponentRegistryTreeNode parentNode = (ComponentRegistryTreeNode) getParent();
        ComponentRegistry parentRegistry = null;
        if (parentNode != null) {
            parentRegistry = parentNode.createHierarchicalComponentRegistry();
        } else {
            parentRegistry = new DefaultComponentRegistry();
        }
        HierarchicalComponentRegistry hierarchicalRegistry = new PublicHierarchicalComponentRegistry(
                parentRegistry,
                componentRegistry
        );
        return hierarchicalRegistry;
    }
}
