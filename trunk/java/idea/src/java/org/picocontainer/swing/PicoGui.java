package org.picocontainer.swing;

import org.picocontainer.gui.tree.ComponentRegistryTreeNode;
import org.picocontainer.gui.tree.ComponentRegistryTreeNodeTestCase;
import org.picocontainer.gui.tree.ComponentTreeNode;
import org.picocontainer.defaults.DefaultComponentRegistry;
import org.picocontainer.internals.ComponentRegistry;

import javax.swing.*;
import java.awt.*;

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

    public static void main(String[] args) {
        ComponentRegistryTreeNode parentNode = new ComponentRegistryTreeNode();

        // register a new child component.
        parentNode.add(new ComponentTreeNode(ComponentRegistryTreeNodeTestCase.Foo.class));

        // register a new child registry with a child component.
        ComponentRegistry childRegistry = new DefaultComponentRegistry();
        ComponentRegistryTreeNode childNode = new ComponentRegistryTreeNode();
        parentNode.add(childNode);
        childNode.add(new ComponentTreeNode(ComponentRegistryTreeNodeTestCase.Bar.class));
        parentNode.add(childNode);

        PicoGui gui = new PicoGui(parentNode);

        JFrame f = new JFrame();
        f.getContentPane().add(gui);

        f.setVisible(true);
        f.pack();
    }
}
