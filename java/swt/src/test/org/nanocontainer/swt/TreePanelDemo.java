package org.nanocontainer.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.nanocontainer.testmodel.DefaultWebServerConfig;
import org.nanocontainer.testmodel.WebServerImpl;

/**
 * @author Laurent Etiemble
 * @version $Revision$
 */
public class TreePanelDemo {
	public static void main(String[] args) {
		// Creation of the dummy container
		DefaultPicoContainer container1 = new DefaultPicoContainer();
		DefaultPicoContainer container2 = new DefaultPicoContainer(container1);
		DefaultPicoContainer container3 = new DefaultPicoContainer(container2);

		container1.registerComponentImplementation(DefaultWebServerConfig.class);
		container1.registerComponentImplementation(WebServerImpl.class);
		container2.registerComponentImplementation(WebServerImpl.class);
		container3.registerComponentImplementation(WebServerImpl.class);

		// SWT in action
		Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setLayout(new GridLayout());

		ContainerTreePanel panel = new ContainerTreePanel(shell, SWT.VERTICAL | SWT.NULL);
		panel.setContainer(container1);

		shell.setSize(400, 300);
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
}
