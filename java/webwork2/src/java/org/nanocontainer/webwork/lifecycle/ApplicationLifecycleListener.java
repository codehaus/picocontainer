package org.nanocontainer.webwork.lifecycle;

import com.opensymphony.xwork.ActionProxyFactory;
import org.nanocontainer.webwork.PicoActionProxyFactory;

import javax.servlet.ServletContextEvent;

/**
 * Created by IntelliJ IDEA.
 * User: chris
 * Date: 26.09.2003
 * Time: 13:22:00
 * @author csturm
 */
public class ApplicationLifecycleListener extends org.nanocontainer.servlet.lifecycle.ApplicationLifecycleListener {
    public void contextInitialized(ServletContextEvent event) {
        ActionProxyFactory.setFactory(new PicoActionProxyFactory());
        super.contextInitialized(event);
    }
}
