package org.picocontainer.swing;

import org.picocontainer.gui.tree.ComponentRegistryTreeNode;
import org.picocontainer.gui.tree.ComponentTreeNode;
import org.picocontainer.internals.ComponentRegistry;
import org.picocontainer.defaults.DefaultComponentRegistry;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoException;
import org.nanocontainer.MethodInvoker;

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
            super("Add Registry");
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
                Class componentImplementation = getClass().getClassLoader().loadClass(componentField.getText());
                ComponentTreeNode componentTreeNode = new ComponentTreeNode(componentImplementation);
                DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();

                int index = getSelectedNode().getChildCount();

                getSelectedNode().insert(componentTreeNode, index);
                int[] newIndexs = new int[] { index };
                treeModel.nodesWereInserted(getSelectedNode(), newIndexs);

            } catch (Exception e) {
                Throwable t = e.getCause() != null ?  e.getCause() : e;
                JOptionPane.showMessageDialog(EditContainerPanel.this, t.getMessage() + " : " + componentField.getText(), "ERROR", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            throw new IllegalStateException("Shouldn't be called when disabled");
        }
    }

    public void addRegistry() {
        if (isRegistrySelected()) {
            try {
                ComponentRegistry componentRegistry = new DefaultComponentRegistry();
                ComponentRegistryTreeNode componentRegistryTreeNode = new ComponentRegistryTreeNode();
                DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();

                int index = getSelectedNode().getChildCount();

                getSelectedNode().insert(componentRegistryTreeNode, index);
                int[] newIndexs = new int[] { index };
                treeModel.nodesWereInserted(getSelectedNode(), newIndexs);

            } catch (Exception e) {
                Throwable t = e.getCause() != null ?  e.getCause() : e;
                JOptionPane.showMessageDialog(EditContainerPanel.this, t.getMessage() + " : " + componentField.getText(), "ERROR", JOptionPane.ERROR_MESSAGE);
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
        ComponentRegistryTreeNode containerNode = (ComponentRegistryTreeNode) selectedNode;
        try {
            ComponentRegistry componentRegistry = containerNode.createHierarchicalComponentRegistry();
            PicoContainer pico = new DefaultPicoContainer.WithComponentRegistry(componentRegistry);
//            pico.instantiateComponents();
            Object[] components = pico.getComponents().toArray();
            new MethodInvoker().invokeMethod("execute", componentRegistry);
        } catch (PicoException e) {
            Throwable t = e.getCause() != null ?  e.getCause() : e;
            JOptionPane.showMessageDialog(EditContainerPanel.this, t.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }

    }

    public boolean isRegistrySelected() {
        return getSelectedNode() instanceof ComponentRegistryTreeNode;
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
