package org.picocontainer.gui.model;

import junit.framework.TestCase;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.*;
import java.beans.IntrospectionException;

import org.picocontainer.gui.swing.ComponentRegistrar;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class ComponentRegistrarTestCase extends TestCase {
    public void testCreateComponentNode() throws IntrospectionException {
        ContainerNode containerNode = new ContainerNode();
        DefaultTreeModel treeModel = new DefaultTreeModel(containerNode);
        ComponentRegistrar componentRegistrar = new ComponentRegistrar(treeModel, null);

        componentRegistrar.createComponentNodeInTree(containerNode, "somekey", String.class);
        ComponentNode componentNode = (ComponentNode) containerNode.getChildAt(0);
        BeanPropertyModel beanPropertyModel = (BeanPropertyModel) componentNode.getUserObject();
        assertEquals(String.class, beanPropertyModel.getBeanClass());
    }

    public void testDisplayComponentNode() throws IntrospectionException {
        ContainerNode containerNode = new ContainerNode();
        DefaultTreeModel treeModel = new DefaultTreeModel(containerNode);

        JTable table = new JTable();

        ComponentRegistrar componentRegistrar = new ComponentRegistrar(treeModel, table);

        componentRegistrar.createComponentNodeInTree(containerNode, "somekey", String.class);
        ComponentNode componentNode = (ComponentNode) containerNode.getChildAt(0);
        BeanPropertyModel beanPropertyModel = (BeanPropertyModel) componentNode.getUserObject();

        componentRegistrar.displayInPropertyTable(beanPropertyModel);
        BeanPropertyTableModel beanPropertyTableModel = (BeanPropertyTableModel) table.getModel();

        assertSame(beanPropertyModel, beanPropertyTableModel.getBeanPropertyModel());
    }

}
