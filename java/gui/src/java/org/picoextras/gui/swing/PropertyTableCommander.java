package org.picoextras.gui.swing;

import org.picoextras.gui.model.ComponentNode;
import org.picoextras.gui.model.BeanPropertyTableModel;
import org.picoextras.gui.model.BeanPropertyModel;

import javax.swing.*;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import java.util.Map;
import java.util.HashMap;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class PropertyTableCommander implements TreeSelectionListener {

    private final ComponentRegistrar componentRegistrar;

    public PropertyTableCommander(JTree tree, ComponentRegistrar componentRegistrar){
        this.componentRegistrar = componentRegistrar;
        tree.addTreeSelectionListener(this);
    }

    public void valueChanged(TreeSelectionEvent evt) {
        Object selected = evt.getPath().getLastPathComponent();
        if(selected instanceof ComponentNode) {
            ComponentNode componentTreeNode = (ComponentNode) selected;
            BeanPropertyModel beanPropertyModel = (BeanPropertyModel) componentTreeNode.getUserObject();
            componentRegistrar.displayInPropertyTable(beanPropertyModel);
        }
    }
}
