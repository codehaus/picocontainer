package org.picocontainer.swing;

import org.picocontainer.gui.tree.ComponentRegistryTreeNode;
import org.picocontainer.gui.tree.ComponentTreeNode;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class PicoGui extends JPanel {

    public PicoGui(ComponentRegistryTreeNode rootNode) {
        setLayout(new BorderLayout());
        JTree tree = new JTree(rootNode);
        tree.setCellRenderer(new PicoTreeCellRenderer());
        JScrollPane scroll = new JScrollPane(tree);
        add(scroll, BorderLayout.CENTER);

        EditContainerPanel editContainerPanel = new EditContainerPanel(tree);
        add(editContainerPanel, BorderLayout.SOUTH);
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

    public static void main(String[] args) {
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
