package org.picocontainer.gui.model;

import junit.framework.TestCase;
import org.picocontainer.gui.swing.EditContainerPanel;

import javax.swing.*;
import javax.swing.tree.TreePath;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class EditContainerPanelTestCase extends TestCase {
    private JTree tree;
    private EditContainerPanel panel;
    private ContainerNode root;

//    protected void setUp() {
//        root = new ContainerNode();
//        tree = new JTree(root);
//        panel = new EditContainerPanel(tree);
//    }
//
//    public void testRegisterComponent() {
//        tree.setSelectionPath(new TreePath(root.getPath()));
//
//        JTextField componentField = panel.getComponentField();
//        componentField.setText(Foo.class.getName());
//
//        panel.addPicoComponent();
//
//        assertEquals(1, root.getChildCount());
//    }
//
//    public void testUnregisterComponent() {
//        testRegisterComponent();
//        ComponentNode fooNode = (ComponentNode) root.getChildAt(0);
//        tree.setSelectionPath(new TreePath(fooNode.getPath()));
//
//        panel.removeSelected();
//
//        assertEquals(0, root.getChildCount());
//    }
//
//    public void testAddContainer() {
//        tree.setSelectionPath(new TreePath(root.getPath()));
//        panel.addPicoContainer();
//        assertEquals(1, root.getChildCount());
//    }
//
//    public void testRemoveContainer() {
//        testAddContainer();
//        ContainerNode fooNode = (ContainerNode) root.getChildAt(0);
//        tree.setSelectionPath(new TreePath(fooNode.getPath()));
//
//        panel.removeSelected();
//
//        assertEquals(0, root.getChildCount());
//    }
//
//
//    public void testExecuteContainer() {
//        tree.setSelectionPath(new TreePath(root.getPath()));
//        JTextField componentField = panel.getComponentField();
//
//        // Make a Foo in the root
//////        componentField.setText(ContainerNodeTestCase.Foo.class.getName());
//        panel.addPicoComponent();
//
//        // Make a Bar in a child
//        panel.addPicoContainer();
//////        componentField.setText(ContainerNodeTestCase.Bar.class.getName());
//        panel.addPicoComponent();
//
//        ContainerNode childRegNode = (ContainerNode) root.getChildAt(1);
//        tree.setSelectionPath(new TreePath(childRegNode.getPath()));
//
//        panel.executeSelected();
//    }
//
}
