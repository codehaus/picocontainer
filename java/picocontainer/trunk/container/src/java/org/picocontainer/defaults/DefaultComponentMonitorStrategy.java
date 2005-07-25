package org.picocontainer.defaults;

import java.io.Serializable;

import org.picocontainer.ComponentMonitor;
import org.picocontainer.ComponentMonitorStrategy;
import org.picocontainer.monitors.NullComponentMonitor;

/**
 * Default implementation of {@link ComponentMonitorStrategy}.
 * It will default to use {@link org.picocontainer.monitors.NullComponentMonitor} if 
 * no monitor is provided.
 *  
 * @author Mauro Talevi
 */
public class DefaultComponentMonitorStrategy implements
        ComponentMonitorStrategy, Serializable {

    private ComponentMonitor monitor;

    /**
     * Creates a DefaultComponentMonitorStrategy with a NullComponentMonitor
     */
    public DefaultComponentMonitorStrategy() {
        this(NullComponentMonitor.getInstance());
    }

    /**
     * Creates a DefaultComponentMonitorStrategy with a given initial monitor
     * @param monitor the ComponentMonitor
     */
    public DefaultComponentMonitorStrategy(ComponentMonitor monitor) {
        this.monitor = monitor;
        checkMonitor();
    }

    public ComponentMonitor currentMonitor() {
        return monitor;
    }

    public void changeMonitor(ComponentMonitor monitor) {
        this.monitor = monitor;
        checkMonitor();        
    }

    /**
     * Checks monitor is valid
     * @throws NullPointerException if monitor is <code>null</code>
     */
    private void checkMonitor() {
        if ( this.monitor == null ){
            throw new NullPointerException("monitor");
        }
    }
    
    /**
     * @return Returns the ComponentMonitor's class name
     * @see Object#toString()
     */
    public String toString() {
        return monitor.getClass().getName();
    }
}
