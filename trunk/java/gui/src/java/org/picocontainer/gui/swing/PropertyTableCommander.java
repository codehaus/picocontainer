package org.picocontainer.gui.swing;

import org.picocontainer.gui.model.ComponentTreeNode;
import org.picocontainer.gui.model.BeanPropertyTableModel;

import javax.swing.*;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.IntrospectionException;
import java.util.Map;
import java.util.HashMap;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class PropertyTableCommander implements TreeSelectionListener {

    private final JTree tree;
    private final JTable table;
    private Map models = new HashMap();

    public PropertyTableCommander(JTree tree, JTable table){
        this.tree = tree;
        this.table = table;

        tree.addTreeSelectionListener(this);
    }

    public void valueChanged(TreeSelectionEvent e) {
        Object selected = e.getPath().getLastPathComponent();
        if(selected instanceof ComponentTreeNode) {
            ComponentTreeNode componentTreeNode = (ComponentTreeNode) selected;
            Class componentImplementation = (Class) componentTreeNode.getUserObject();
            try {
                BeanInfo beanInfo = Introspector.getBeanInfo(componentImplementation);
                BeanPropertyTableModel model = (BeanPropertyTableModel) models.get(componentTreeNode);
                if(model==null){
                    model = new BeanPropertyTableModel(beanInfo);
                    models.put(componentTreeNode,model);
                }
                table.setModel(model);
            } catch (IntrospectionException e1) {
                JOptionPane.showMessageDialog(tree, e1.getStackTrace(), e1.getMessage(), JOptionPane.ERROR_MESSAGE);
            }
        } else {
            table.setModel(BeanPropertyTableModel.EMPTY_MODEL);
        }
    }
}
