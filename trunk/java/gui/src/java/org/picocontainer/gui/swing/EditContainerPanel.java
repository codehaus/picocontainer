package org.picocontainer.gui.swing;

import javax.swing.*;
import javax.swing.text.Document;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class EditContainerPanel extends JPanel {

//    private class AddRegistryAction extends AbstractAction {
//        public AddRegistryAction() {
//            super("Add Container");
//        }
//
//        public void actionPerformed(ActionEvent evt) {
//            addPicoContainer();
//        }
//    }
//
//    private class RemoveNodeAction extends AbstractAction {
//        public RemoveNodeAction() {
//            super("Remove");
//        }
//
//        public void actionPerformed(ActionEvent evt) {
//            removeSelected();
//        }
//    }
//
//    private class ExecuteSelectedAction extends AbstractAction {
//        public ExecuteSelectedAction() {
//            super("Execute");
//        }
//
//        public void actionPerformed(ActionEvent evt) {
//            executeSelected();
//        }
//
//    }

    private final JButton addPicoComponentButton;

//    private final AddRegistryAction addRegistryAction;
//    private final JButton addRegistryButton;
//
//    private final RemoveNodeAction removeNodeAction;
//    private final JButton removeNodeButton;
//
//    private final ExecuteSelectedAction executeSelectedAction;
    private final JButton executeContainerButton;

    private final JTextField componentField;

    public EditContainerPanel(AddPicoComponentAction addPicoComponentAction,
                              ExecuteContainerAction executeContainerAction,
                              Document componentImplementationDocument) {

        addPicoComponentButton = new JButton(addPicoComponentAction);

//        addRegistryAction = new AddRegistryAction();
//        addRegistryButton = new JButton(addRegistryAction);
//
//        removeNodeAction = new RemoveNodeAction();
//        removeNodeButton = new JButton(removeNodeAction);
//
        executeContainerButton = new JButton(executeContainerAction);

        componentField = new JTextField(componentImplementationDocument,PicoGui.A.class.getName(), 25);
        componentField.addActionListener(addPicoComponentAction);

        add(componentField);
        add(addPicoComponentButton);
//        add(addRegistryButton);
//        add(removeNodeButton);
        add(executeContainerButton);
    }

    public JTextField getComponentField() {
        return componentField;
    }


//    public Action getAddRegistryAction() {
//        return addRegistryAction;
//    }

//    public void addPicoContainer() {
//        if (isRegistrySelected()) {
//            try {
//                ContainerNode componentRegistryTreeNode = new ContainerNode();
//                DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();
//
//                int index = getSelectedNode().getChildCount();
//
//                getSelectedNode().insert(componentRegistryTreeNode, index);
//                int[] newIndexs = new int[] { index };
//                treeModel.nodesWereInserted(getSelectedNode(), newIndexs);
//
//            } catch (Exception e) {
//                Throwable t = e.getCause() != null ?  e.getCause() : e;
//                JOptionPane.showMessageDialog(tree, t.getStackTrace(), t.getClass().getName() + " " + t.getMessage(), JOptionPane.ERROR_MESSAGE);
//            }
//        } else {
//            throw new IllegalStateException("Shouldn't be called when disabled");
//        }
//    }
//
//    public void removeSelected() {
//        DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();
//
//        TreeNode parent = getSelectedNode().getParent();
//        int index = parent.getIndex(getSelectedNode());
//
//        getSelectedNode().removeFromParent();
//        int[] newIndexs = new int[] { index };
//        treeModel.nodesWereRemoved(parent, newIndexs, new Object[]{getSelectedNode()});
//    }
//
//    // TODO: execute only one of the components should be possible too.
//    public void executeSelected() {
//        TreeNode selectedNode = getSelectedNode();
//        try {
//            if(selectedNode instanceof ComponentNode) {
//                ComponentNode node = (ComponentNode) selectedNode;
//                ComponentAdapter componentAdapter = (ComponentAdapter) node.getUserObject();
//
//                ContainerNode parent = (ContainerNode) node.getParent();
//////                AbstractPicoContainer reg = parent.createPicoContainer();
//////                Object component = componentAdapter.getComponentInstance(reg);
//            } else {
//                ContainerNode containerNode = (ContainerNode) selectedNode;
//                PicoContainer pico = containerNode.createPicoContainer();
//                pico.getComponentInstances();
//            }
//        } catch (PicoException e) {
//            Throwable t = e.getCause() != null ?  e.getCause() : e;
//            JOptionPane.showMessageDialog(tree, t.getStackTrace(), t.getClass().getName() + " " + t.getMessage(), JOptionPane.ERROR_MESSAGE);
//        }
//    }
//
//    public boolean isRegistrySelected() {
//        return getSelectedNode() instanceof ContainerNode;
//    }
//
//    private DefaultMutableTreeNode getSelectedNode() {
//        TreePath path = tree.getSelectionPath();
//        if(path != null) {
//            return (DefaultMutableTreeNode) path.getLastPathComponent();
//        } else {
//            return null;
//        }
//    }
}
