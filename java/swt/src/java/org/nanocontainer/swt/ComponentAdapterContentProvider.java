package org.nanocontainer.swt;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.picocontainer.ComponentAdapter;
import org.nanocontainer.guimodel.ComponentAdapterModel;

/**
 * Provides the data (BeanProperty) to be put inside a table.
 *
 * @author Laurent Etiemble
 * @version $Revision$
 */
public class ComponentAdapterContentProvider implements IStructuredContentProvider {
	private ComponentAdapterModel model = null;

	public ComponentAdapterContentProvider() {
		super();
	}

	public Object[] getElements(Object object) {
		Object[] result = null;
		if (model != null) {
			result = model.getProperties();
		}
		return result;
	}

	public void inputChanged(Viewer viewer, Object oldValue, Object newValue) {
		if (newValue != null) {
            ComponentAdapter componentAdapter = (ComponentAdapter) newValue;
			model = ComponentAdapterModel.getInstance(componentAdapter);
		}
	}

	public void dispose() {
		// Do nothing
	}
}
