package org.nanocontainer.swing;

import org.picocontainer.defaults.DefaultPicoContainer;
import org.nanocontainer.testmodel.DefaultWebServerConfig;
import org.nanocontainer.testmodel.WebServerImpl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author Laurent Etiemble
 * @version $Revision$
 */
public class TreeDemo {
	public static void main(String[] args) {
		// Creation of the dummy container
		DefaultPicoContainer container1 = new DefaultPicoContainer();
		DefaultPicoContainer container2 = new DefaultPicoContainer(container1);
		DefaultPicoContainer container3 = new DefaultPicoContainer(container2);

		container1.registerComponentImplementation(DefaultWebServerConfig.class);
		container1.registerComponentImplementation(WebServerImpl.class);
		container2.registerComponentImplementation(WebServerImpl.class);
		container3.registerComponentImplementation(WebServerImpl.class);

		// Swing in action
		ContainerTree tree = new ContainerTree(container1, IconHelper.getIcon(IconHelper.DEFAULT_COMPONENT_ICON, false));

		JFrame frame = new JFrame();
		frame.getContentPane().add(tree, BorderLayout.CENTER);
		frame.pack();
		frame.show();
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				System.exit(0);
			}
		});
	}
}
