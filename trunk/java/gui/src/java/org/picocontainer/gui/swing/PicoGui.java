package org.picocontainer.gui.swing;

import org.picocontainer.gui.model.*;

import javax.swing.*;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class PicoGui extends JPanel {

    public PicoGui(ContainerNode rootNode) {
        super(new BorderLayout());

        JSplitPane split = new JSplitPane();
        JPanel left = new JPanel(new BorderLayout());

        DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);
        JTree tree = new JTree(treeModel);
        tree.setCellRenderer(new PicoTreeCellRenderer());
        left.add(new JScrollPane(tree), BorderLayout.CENTER);

        JTable table = new JTable(BeanPropertyTableModel.EMPTY_MODEL);

        // Set up a ComponentRegistrar
        ComponentRegistrar componentRegistrar = new ComponentRegistrar(treeModel, table);

        // Set up a PropertyTableCommander
        PropertyTableCommander propertyTableCommander = new PropertyTableCommander(tree, componentRegistrar);

        // Set up an AddPicoComponentAction
        Document componentImplementationDocument = new PlainDocument();
        AddPicoComponentAction addPicoComponentAction = new AddPicoComponentAction(
                componentRegistrar,
                tree,
                tree,
                componentImplementationDocument);

        ExecuteContainerAction executeContainerAction = new ExecuteContainerAction(tree);

        EditContainerPanel editContainerPanel = new EditContainerPanel(
                addPicoComponentAction,
                executeContainerAction,
                componentImplementationDocument
        );
        left.add(editContainerPanel, BorderLayout.NORTH);

        JPanel right = new JPanel(new BorderLayout());

        right.add(new JScrollPane(table),  BorderLayout.CENTER);

        split.setTopComponent(left);
        split.setBottomComponent(right);
        split.setOrientation(JSplitPane.VERTICAL_SPLIT);

        add(split, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        ContainerNode parentNode = new ContainerNode();
        PicoGui gui = new PicoGui(parentNode);

        JFrame f = new JFrame();
        f.getContentPane().add(gui);

        f.setVisible(true);
        f.pack();
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

        public String getMessage() {
            return message;
        }
    }
}
