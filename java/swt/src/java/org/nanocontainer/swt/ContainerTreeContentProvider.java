package org.picoextras.swt;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.picocontainer.PicoContainer;
import org.picoextras.guimodel.ContainerModel;

import java.util.Collection;

/**
 * Provides the data to be put inside a tree.
 * 
 * @author Laurent Etiemble
 * @version $Revision$
 */
public class ContainerTreeContentProvider extends ContainerModel implements ITreeContentProvider {
	public ContainerTreeContentProvider(PicoContainer pico) {
        super(pico);
	}

	public Object[] getChildren(Object parent) {
        return getAllChildren(parent);
	}

	public Object[] getElements(Object root) {
		Object[] result = null;
		if (root instanceof Collection) {
			result = ((Collection) root).toArray();
		}
		return result;
	}

	public Object getParent(Object child) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

    public boolean hasChildren(Object node) {
        return getAllChildren(node).length > 0;
    }

    public void inputChanged(Viewer viewer, Object oldValue, Object newValue) {
		// Do nothing
	}

	public void dispose() {
		// Do nothing
	}
}
