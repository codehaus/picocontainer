package org.picoextras.swing;

import org.picocontainer.MutablePicoContainer;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreePath;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import java.util.Arrays;

/**
 * Simple tree that takes a PicoContainer as root object.
 * 
 * @author Laurent Etiemble
 * @version $Revision$
 */
public class ContainerTree extends JTree {

	public ContainerTree(MutablePicoContainer mutablePicoContainer, Icon componentIcon) {
		super(new ContainerTreeModel(mutablePicoContainer));
		this.setRootVisible(true);
		this.setCellRenderer(new ContainerTreeCellRenderer(componentIcon));

        getModel().addTreeModelListener(new TreeModelListener(){
            public void treeNodesChanged(TreeModelEvent e) {

            }

            public void treeNodesInserted(final TreeModelEvent e) {
            }

            public void treeNodesRemoved(TreeModelEvent e) {

            }

            public void treeStructureChanged(final TreeModelEvent e) {
                SwingUtilities.invokeLater(new Runnable(){
                    public void run() {
                        System.out.println("e = " + Arrays.asList(e.getTreePath().getPath()));
                        final Object lastChild = e.getChildren()[e.getChildren().length - 1];
                        TreePath newPath = new TreePath(e.getTreePath().pathByAddingChild(lastChild));
                        setSelectionPath(newPath);
                    }
                });
            }
        });

        addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                Object path = Arrays.asList(e.getPath().getPath());
                System.out.println("path = " + path);
            }
        });
	}
}
