package org.picocontainer.gui.swing;

import org.picocontainer.gui.model.*;

import javax.swing.*;
import javax.swing.tree.TreeModel;
import java.awt.*;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class PicoGui extends JPanel {

    public PicoGui(ComponentRegistryTreeNode rootNode) {
        super(new BorderLayout());

        JSplitPane split = new JSplitPane();
        JPanel left = new JPanel(new BorderLayout());

        TreeModel treeModel = new PicoTreeModel(rootNode);
        JTree tree = new JTree(treeModel);
        tree.setCellRenderer(new PicoTreeCellRenderer());
        left.add(new JScrollPane(tree), BorderLayout.CENTER);

        EditContainerPanel editContainerPanel = new EditContainerPanel(tree);
        left.add(editContainerPanel, BorderLayout.NORTH);

        JPanel right = new JPanel(new BorderLayout());
        JTable table = new JTable(BeanPropertyTableModel.EMPTY_MODEL);

        new PropertyTableCommander(tree, table);

        right.add(new JScrollPane(table),  BorderLayout.CENTER);

        split.setTopComponent(left);
        split.setBottomComponent(right);
        split.setOrientation(JSplitPane.VERTICAL_SPLIT);

        add(split, BorderLayout.CENTER);
    }

    public static class A {
        private String message;

        public void execute() {
            System.out.println(message);
        }

        public A() {
            System.out.println("CONSTRUCTED");
        }

        public void setMessage(String message) {
            this.message = message;
            System.out.println("SET:" + message);
        }
    }

    public static void main(String[] args) {
        ComponentRegistryTreeNode parentNode = new ComponentRegistryTreeNode();
        PicoGui gui = new PicoGui(parentNode);

        JFrame f = new JFrame();
        f.getContentPane().add(gui);

        f.setVisible(true);
        f.pack();
    }
}
