package org.picocontainer.gui.model;

import org.picocontainer.extras.BeanPropertyComponentAdapterFactory;
import org.picocontainer.extras.InvokingComponentAdapterFactory;
import org.picocontainer.defaults.DefaultComponentAdapterFactory;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.defaults.ComponentAdapter;
import org.picocontainer.PicoIntrospectionException;

import javax.swing.tree.DefaultTreeModel;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class PicoTreeModel extends DefaultTreeModel {

    private final ComponentAdapterFactory componentAdapterFactory;

    public PicoTreeModel(ComponentRegistryTreeNode treeNode) {
        super(treeNode);

        componentAdapterFactory =
                new InvokingComponentAdapterFactory(
                        new BeanPropertyComponentAdapterFactory(
                                new DefaultComponentAdapterFactory()
                        ),
                        "execute",
                        null,
                        null
                );
    }

    public void insertComponentIntoRegistry(String componentClass, ComponentRegistryTreeNode parentNode) throws ClassNotFoundException, PicoIntrospectionException {
        Class componentImplementation = getClass().getClassLoader().loadClass(componentClass);

        ComponentAdapter invokingAdapter = componentAdapterFactory.createComponentAdapter(componentImplementation, componentImplementation, null);
        ComponentTreeNode componentTreeNode = new ComponentTreeNode(invokingAdapter);

        int index = parentNode.getChildCount();
        insertNodeInto(componentTreeNode, parentNode, index);
    }
}
