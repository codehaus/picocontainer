package org.picoextras.gui.swing;

import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.tree.TreePath;
import javax.swing.*;

/**
 * This class makes sure new nodes are expanded and selected.
 *
 * @pico
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class TreeExpander implements TreeModelListener {

    private final JTree tree;

    public TreeExpander(JTree tree) {
        this.tree = tree;
        tree.getModel().addTreeModelListener(this);
    }

    public void treeNodesInserted(final TreeModelEvent evt) {
        Object[] children = evt.getChildren();
        final TreePath[] childPaths = new TreePath[children.length];

        for (int i = 0; i < children.length; i++) {
            Object child = children[i];
            childPaths[i] = evt.getTreePath().pathByAddingChild(child);
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                tree.setSelectionPaths(childPaths);
            }
        });
    }

    public void treeNodesChanged(TreeModelEvent evt) {
    }

    public void treeNodesRemoved(TreeModelEvent evt) {
    }

    public void treeStructureChanged(TreeModelEvent evt) {
    }
}
