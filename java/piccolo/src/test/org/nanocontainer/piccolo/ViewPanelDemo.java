package org.picoextras.piccolo;

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
public class ViewPanelDemo {
	public static void main(String[] args) {
        /*
        1
          4
          2
            3
              5
                6
        */
		DefaultPicoContainer container1 = new DefaultPicoContainer();
		DefaultPicoContainer container2 = new DefaultPicoContainer(container1);
		DefaultPicoContainer container3 = new DefaultPicoContainer(container2);
		DefaultPicoContainer container4 = new DefaultPicoContainer(container1);
		DefaultPicoContainer container5 = new DefaultPicoContainer(container3);
		DefaultPicoContainer container6 = new DefaultPicoContainer(container5);

		container1.registerComponentImplementation(DefaultWebServerConfig.class);
		container1.registerComponentImplementation(WebServerImpl.class);
		container2.registerComponentImplementation(WebServerImpl.class);
		container3.registerComponentImplementation(WebServerImpl.class);
		container4.registerComponentImplementation(DefaultWebServerConfig.class);
		container5.registerComponentImplementation(DefaultWebServerConfig.class);
		container5.registerComponentImplementation(WebServerImpl.class);
		container6.registerComponentImplementation(WebServerImpl.class);

		// Piccolo in action
		ContainerViewPanel panel = new ContainerViewPanel(container1);

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
