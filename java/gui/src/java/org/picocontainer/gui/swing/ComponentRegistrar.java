package org.picocontainer.gui.swing;

import org.picocontainer.gui.model.ContainerNode;
import org.picocontainer.gui.model.BeanPropertyModel;
import org.picocontainer.gui.model.ComponentNode;
import org.picocontainer.gui.model.BeanPropertyTableModel;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.*;
import java.beans.IntrospectionException;

public class ComponentRegistrar {
    private final DefaultTreeModel treeModel;
    private final JTable table;

    public ComponentRegistrar(DefaultTreeModel treeModel, JTable table) {
        this.treeModel = treeModel;
        this.table = table;
    }

    public void createComponentNodeInTree(ContainerNode containerNode, Object componentKey, Class componentImplementation) throws IntrospectionException {
        BeanPropertyModel beanPropertyModel = new BeanPropertyModel(componentImplementation);
        ComponentNode componentNode = new ComponentNode(beanPropertyModel);
        treeModel.insertNodeInto(componentNode, containerNode, 0);
    }

    public void displayInPropertyTable(BeanPropertyModel beanPropertyModel) {
        BeanPropertyTableModel beanPropertyTableModel = new BeanPropertyTableModel(beanPropertyModel);
        table.setModel(beanPropertyTableModel);
    }
}
