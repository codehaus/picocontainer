package org.picocontainer.gui.swing;

import junit.framework.TestCase;

import javax.swing.*;
import javax.swing.table.TableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.DefaultTreeModel;

import org.picocontainer.gui.model.ComponentTreeNode;
import org.picocontainer.gui.model.BeanPropertyTableModel;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class PropertyTableCommanderTest extends TestCase {

    public void testTreeSelectionUpdatesTable() {
        JTree tree = new JTree();
        TableModel defaultModel = new DefaultTableModel();
        JTable table = new JTable(defaultModel);

        PropertyTableCommander propertyTableCommander = new PropertyTableCommander(tree, table);

        DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();
        treeModel.setRoot(new ComponentTreeNode(BeanPropertyTableModelTestCase.Man.class));

        tree.setSelectionPath(new TreePath(treeModel.getRoot()));
        BeanPropertyTableModel model = (BeanPropertyTableModel) table.getModel();

        assertEquals("birth", model.getValueAt(0,0));
    }
}
