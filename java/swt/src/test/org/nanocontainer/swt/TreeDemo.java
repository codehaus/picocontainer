package org.picoextras.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picoextras.testmodel.DefaultWebServerConfig;
import org.picoextras.testmodel.WebServerImpl;

/**
 * @author Laurent Etiemble
 * @version $Revision$
 */
public class TreeDemo {
	public static void main(String[] args) {
		// Creation of the dummy container
		MutablePicoContainer container1 = new DefaultPicoContainer();
		container1.registerComponentImplementation(WebServerImpl.class);
		container1.registerComponentImplementation(DefaultWebServerConfig.class);
		MutablePicoContainer container2 = new DefaultPicoContainer();
        container2.registerComponentImplementation(WebServerImpl.class);
        container2.registerComponentImplementation(DefaultWebServerConfig.class);
		container1.registerComponentInstance(container2);

		// SWT in action
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new GridLayout());

		ContainerTreeViewer viewer = new ContainerTreeViewer(shell, SWT.BORDER | SWT.SINGLE);
		viewer.setContainer(container1);

		shell.setSize(300, 200);
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
}
