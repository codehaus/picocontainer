/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
package org.picoextras.swing.action;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.ComponentAdapter;
import org.picoextras.swing.ContainerTree;
import org.picoextras.swing.ContainerTreeModel;
import org.picoextras.swing.IconHelper;

import javax.swing.AbstractAction;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

/**
 * Action that listens to tree selections and enables/disables itself accordingly.
 *
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public abstract class TreeSelectionAction extends AbstractAction {
    protected final ContainerTreeModel containerTreeModel;

    protected MutablePicoContainer selectedContainer;
    protected ComponentAdapter selectedAdapter;
    protected Object selected;

    protected TreeSelectionAction(String name, String imagePath, ContainerTree tree) {
        super(name, IconHelper.getIcon(imagePath, false));
        containerTreeModel = (ContainerTreeModel) tree.getModel();
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                selected = e.getPath().getLastPathComponent();
                System.out.println("selected = " + selected);
                if(selected instanceof MutablePicoContainer) {
                    selectedContainer = (MutablePicoContainer) selected;
                    selectedAdapter = null;
                } else {
                    selectedAdapter = (ComponentAdapter) selected;
                    selectedContainer = null;
                }
                setEnabled();
            }
        });
        setEnabled();
    }

    protected abstract void setEnabled();
}