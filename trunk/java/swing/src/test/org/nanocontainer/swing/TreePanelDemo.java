package org.picoextras.swing;

import org.picocontainer.defaults.DefaultPicoContainer;
import org.picoextras.testmodel.DefaultWebServerConfig;
import org.picoextras.testmodel.WebServerImpl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author Laurent Etiemble
 * @version $Revision$
 */
public class TreePanelDemo {
	public static void main(String[] args) {
		// Creation of the dummy container
		DefaultPicoContainer container1 = new DefaultPicoContainer();
		DefaultPicoContainer container2 = new DefaultPicoContainer(container1);
        container1.registerComponentInstance(container2);
		DefaultPicoContainer container3 = new DefaultPicoContainer(container2);

		container1.registerComponentImplementation(DefaultWebServerConfig.class);
		container1.registerComponentImplementation(WebServerImpl.class);
		container2.registerComponentImplementation(WebServerImpl.class);
		container3.registerComponentImplementation(WebServerImpl.class);

		ContainerTreePanel panel = new ContainerTreePanel(container1, new JLabel("Start"));

		JFrame frame = new JFrame();
		frame.getContentPane().add(panel, BorderLayout.CENTER);
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
