package org.picocontainer.gui.swing;

import org.picocontainer.gui.model.*;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.ComponentAdapter;

import javax.swing.*;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.TreePath;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.awt.event.ActionEvent;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class EditContainerPanel extends JPanel {

    private class RegisterComponentAction extends AbstractAction {
        public RegisterComponentAction() {
            super("Register Component");
        }

        public void actionPerformed(ActionEvent evt) {
            registerComponent();
        }
    }

    private class AddRegistryAction extends AbstractAction {
        public AddRegistryAction() {
            super("Add Container");
        }

        public void actionPerformed(ActionEvent evt) {
            addRegistry();
        }
    }

    private class RemoveNodeAction extends AbstractAction {
        public RemoveNodeAction() {
            super("Remove");
        }

        public void actionPerformed(ActionEvent evt) {
            removeSelected();
        }
    }

    private class ExecuteSelectedAction extends AbstractAction {
        public ExecuteSelectedAction() {
            super("Execute");
        }

        public void actionPerformed(ActionEvent evt) {
            executeSelected();
        }

    }

    private final JTree tree;

    private final RegisterComponentAction registerComponentAction;
    private final JButton registerComponentButton;

    private final AddRegistryAction addRegistryAction;
    private final JButton addRegistryButton;

    private final RemoveNodeAction removeNodeAction;
    private final JButton removeNodeButton;

    private final ExecuteSelectedAction executeSelectedAction;
    private final JButton executeSelectedButton;

    private final JTextField componentField;

    public EditContainerPanel(JTree tree) {
        this.tree = tree;

        registerComponentAction = new RegisterComponentAction();
        registerComponentButton = new JButton(registerComponentAction);

        addRegistryAction = new AddRegistryAction();
        addRegistryButton = new JButton(addRegistryAction);

        removeNodeAction = new RemoveNodeAction();
        removeNodeButton = new JButton(removeNodeAction);

        executeSelectedAction = new ExecuteSelectedAction();
        executeSelectedButton = new JButton(executeSelectedAction);

        componentField = new JTextField(25);
        componentField.addActionListener(registerComponentAction);

        add(componentField);
        add(registerComponentButton);
        add(addRegistryButton);
        add(removeNodeButton);
        add(executeSelectedButton);

        tree.addTreeSelectionListener(new TreeSelectionListener(){
            public void valueChanged(TreeSelectionEvent e) {
                registerComponentAction.setEnabled(isRegistrySelected());
                registerComponentButton.setEnabled(isRegistrySelected());

                addRegistryAction.setEnabled(isRegistrySelected());
                addRegistryButton.setEnabled(isRegistrySelected());
            }
        });
    }

    public JTextField getComponentField() {
        return componentField;
    }

    public Action getRegisterComponentAction() {
        return registerComponentAction;
    }

    public Action getAddRegistryAction() {
        return addRegistryAction;
    }

    public void registerComponent() {
        if (isRegistrySelected()) {
            try {
                DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();
                String className = componentField.getText();
                Class componentImplementation = getClass().getClassLoader().loadClass(className);

                // This model will be used by the node (which will set the props)
                // and also by the table, which lets the props be edited
                BeanPropertyModel beanPropertyModel = new BeanPropertyModel(componentImplementation);
                ComponentNode componentNode = new ComponentNode(beanPropertyModel);
                ContainerNode containerNode = (ContainerNode) getSelectedNode();
                treeModel.insertNodeInto(componentNode, containerNode, 0);
            } catch (Exception e) {
                Throwable t = e.getCause() != null ?  e.getCause() : e;
                JOptionPane.showMessageDialog(tree, t.getStackTrace(), t.getClass().getName() + " " + t.getMessage(), JOptionPane.ERROR_MESSAGE);
            }
        } else {
            throw new IllegalStateException("Shouldn't be called when disabled");
        }
    }

    public void addRegistry() {
        if (isRegistrySelected()) {
            try {
                ContainerNode componentRegistryTreeNode = new ContainerNode();
                DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();

                int index = getSelectedNode().getChildCount();

                getSelectedNode().insert(componentRegistryTreeNode, index);
                int[] newIndexs = new int[] { index };
                treeModel.nodesWereInserted(getSelectedNode(), newIndexs);

            } catch (Exception e) {
                Throwable t = e.getCause() != null ?  e.getCause() : e;
                JOptionPane.showMessageDialog(tree, t.getStackTrace(), t.getClass().getName() + " " + t.getMessage(), JOptionPane.ERROR_MESSAGE);
            }
        } else {
            throw new IllegalStateException("Shouldn't be called when disabled");
        }
    }

    public void removeSelected() {
        DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();

        TreeNode parent = getSelectedNode().getParent();
        int index = parent.getIndex(getSelectedNode());

        getSelectedNode().removeFromParent();
        int[] newIndexs = new int[] { index };
        treeModel.nodesWereRemoved(parent, newIndexs, new Object[]{getSelectedNode()});
    }

    // TODO: execute only one of the components should be possible too.
    public void executeSelected() {
        TreeNode selectedNode = getSelectedNode();
        try {
            if(selectedNode instanceof ComponentNode) {
                ComponentNode node = (ComponentNode) selectedNode;
                ComponentAdapter componentAdapter = (ComponentAdapter) node.getUserObject();

                ContainerNode parent = (ContainerNode) node.getParent();
////                AbstractPicoContainer reg = parent.createPicoContainer();
////                Object component = componentAdapter.getComponentInstance(reg);
            } else {
                ContainerNode containerNode = (ContainerNode) selectedNode;
////                AbstractPicoContainer componentRegistry = containerNode.createPicoContainer();
////                Collection components = componentRegistry.getComponentInstances();
            }
        } catch (PicoInitializationException e) {
            Throwable t = e.getCause() != null ?  e.getCause() : e;
            JOptionPane.showMessageDialog(tree, t.getStackTrace(), t.getClass().getName() + " " + t.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isRegistrySelected() {
        return getSelectedNode() instanceof ContainerNode;
    }

    private DefaultMutableTreeNode getSelectedNode() {
        TreePath path = tree.getSelectionPath();
        if(path != null) {
            return (DefaultMutableTreeNode) path.getLastPathComponent();
        } else {
            return null;
        }
    }
}
