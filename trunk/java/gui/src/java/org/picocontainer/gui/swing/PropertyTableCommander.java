package org.picocontainer.gui.swing;

import org.picocontainer.gui.model.ComponentNode;
import org.picocontainer.gui.model.BeanPropertyTableModel;
import org.picocontainer.gui.model.BeanPropertyModel;

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
        if(selected instanceof ComponentNode) {
            ComponentNode componentTreeNode = (ComponentNode) selected;
            Object nodeValue = componentTreeNode.getUserObject();
            try {
                BeanPropertyTableModel model = (BeanPropertyTableModel) models.get(nodeValue);
                if(model==null){
                    Class componentImplementation = (Class) nodeValue;
                    BeanPropertyModel beanPropertyModel = new BeanPropertyModel(componentImplementation);
                    model = new BeanPropertyTableModel(beanPropertyModel);
                    models.put(nodeValue,model);
                }
                table.setModel(model);
            } catch (Exception e) {
                Throwable t = e.getCause() != null ?  e.getCause() : e;
                JOptionPane.showMessageDialog(tree, t.getStackTrace(), t.getClass().getName() + " " + t.getMessage(), JOptionPane.ERROR_MESSAGE);
            }
        } else {
            table.setModel(BeanPropertyTableModel.EMPTY_MODEL);
        }
    }
}
