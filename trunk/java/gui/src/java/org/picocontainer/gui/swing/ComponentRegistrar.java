package org.picocontainer.gui.swing;

import org.picocontainer.gui.model.ContainerNode;
import org.picocontainer.gui.model.BeanPropertyModel;
import org.picocontainer.gui.model.ComponentNode;
import org.picocontainer.gui.model.BeanPropertyTableModel;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.*;
import java.beans.IntrospectionException;

public class ComponentRegistrar {
    private final TreeModel treeModel;
    private final JTable table;

    public ComponentRegistrar(TreeModel treeModel, JTable table) {
        this.treeModel = treeModel;
        this.table = table;
    }

    public void createComponentNodeInTree(ContainerNode containerNode, Object componentKey, Class componentImplementation) throws IntrospectionException {
        BeanPropertyModel beanPropertyModel = new BeanPropertyModel(componentImplementation);
        ComponentNode componentNode = new ComponentNode(beanPropertyModel);
        //treeModel.insertNodeInto(componentNode, containerNode, 0);

        // now make a child node of the component displaying the property table.
        DefaultMutableTreeNode propNode = new DefaultMutableTreeNode(new BeanPropertyTableModel(beanPropertyModel));
        //treeModel.insertNodeInto(propNode, componentNode, 0);
    }

    public void displayInPropertyTable(BeanPropertyModel beanPropertyModel) {
        BeanPropertyTableModel beanPropertyTableModel = new BeanPropertyTableModel(beanPropertyModel);
        table.setModel(beanPropertyTableModel);
    }
}
