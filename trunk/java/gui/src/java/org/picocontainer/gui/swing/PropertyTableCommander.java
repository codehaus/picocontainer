package org.picocontainer.gui.swing;

import org.picocontainer.gui.model.ComponentTreeNode;
import org.picocontainer.gui.model.BeanPropertyTableModel;
import org.picocontainer.extras.BeanPropertyComponentAdapterFactory;
import org.picocontainer.extras.InvokingComponentAdapterFactory;

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

    private final JTree tree;
    private final JTable table;
    private Map models = new HashMap();

    public PropertyTableCommander(JTree tree, JTable table){
        this.tree = tree;
        this.table = table;

        tree.addTreeSelectionListener(this);
    }

    public void valueChanged(TreeSelectionEvent evt) {
        Object selected = evt.getPath().getLastPathComponent();
        if(selected instanceof ComponentTreeNode) {
            ComponentTreeNode componentTreeNode = (ComponentTreeNode) selected;
            Object nodeValue = componentTreeNode.getUserObject();
            try {
                BeanPropertyTableModel model = (BeanPropertyTableModel) models.get(nodeValue);
                if(model==null){
                    InvokingComponentAdapterFactory.Adapter ia = (InvokingComponentAdapterFactory.Adapter) nodeValue;
                    BeanPropertyComponentAdapterFactory.Adapter ba = (BeanPropertyComponentAdapterFactory.Adapter) ia.getDelegate();
                    model = new BeanPropertyTableModel(ba);
                    models.put(nodeValue,model);
                }
                table.setModel(model);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(tree, e.getStackTrace(), e.getMessage(), JOptionPane.ERROR_MESSAGE);
            }
        } else {
            table.setModel(BeanPropertyTableModel.EMPTY_MODEL);
        }
    }
}
