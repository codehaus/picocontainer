package org.picocontainer.swing;

import org.picocontainer.gui.tree.ComponentRegistryTreeNode;
import org.picocontainer.gui.tree.ComponentTreeNode;
import org.picocontainer.gui.swing.BeanPropertyTableModel;
import org.picocontainer.gui.swing.BeanPropertyTableModelTestCase;
import org.picocontainer.gui.swing.PropertyTableCommander;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.beans.Introspector;
import java.beans.IntrospectionException;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class PicoGui extends JPanel {

    public PicoGui(ComponentRegistryTreeNode rootNode) throws IntrospectionException {
        super(new BorderLayout());

        JSplitPane split = new JSplitPane();

        JPanel left = new JPanel(new BorderLayout());

        JTree tree = new JTree(rootNode);
        tree.setCellRenderer(new PicoTreeCellRenderer());
        left.add(new JScrollPane(tree), BorderLayout.CENTER);

        EditContainerPanel editContainerPanel = new EditContainerPanel(tree);
        left.add(editContainerPanel, BorderLayout.SOUTH);

        JPanel right = new JPanel(new BorderLayout());
        JTable table = new JTable(BeanPropertyTableModel.EMPTY_MODEL);

        new PropertyTableCommander(tree, table);

        right.add(new JScrollPane(table),  BorderLayout.CENTER);

        split.setLeftComponent(left);
        split.setRightComponent(right);

        add(split, BorderLayout.CENTER);
    }

    public static class A {
        private String message;

        public void execute() {
            System.out.println(message);
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public static void main(String[] args) throws IntrospectionException {
        ComponentRegistryTreeNode parentNode = new ComponentRegistryTreeNode();

        // register a new child component.
        parentNode.add(new ComponentTreeNode(A.class));

        // register a new child registry with a child component.
        ComponentRegistryTreeNode childNode = new ComponentRegistryTreeNode();
        parentNode.add(childNode);
        childNode.add(new ComponentTreeNode(ArrayList.class));
        parentNode.add(childNode);

        PicoGui gui = new PicoGui(parentNode);

        JFrame f = new JFrame();
        f.getContentPane().add(gui);

        f.setVisible(true);
        f.pack();
    }
}
