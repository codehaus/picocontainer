package cdibook.jmx;

import junit.framework.TestCase;
import javax.management.ObjectName;
import javax.management.MBeanInfo;
import javax.management.MBeanAttributeInfo;
import org.nanocontainer.jmx;

/**
 * @author Michael Ward
 * @version $Revision$
 */

public class JmxTestCase extends TestCase {

    public void testSampleService() {
        // START SNIPPET: mbeaninfo        
        MBeanAttributeInfo[] attributes = new MBeanAttributeInfo[1];
        attributes[0] = new MBeanAttributeInfo("Count", 
                                                int.class.toString(),
                                                "this is a description for the attribute", 
                                                true, 
                                                false, 
                                                false);
                                                
        MBeanInfo mBeanInfo = new MBeanInfo(SampleService.class.getName(), 
                                            "description for the MBean", 
                                            attributes, 
                                            null, 
                                            null, 
                                            null);
        // END SNIPPET: mbeaninfo
        // START SNIPPET: jmxvisitor        
        JMXVisitor visitor = new JMXVisitor();
        ObjectName objectName = new ObjectName("domian-name:hello=world");
        jmxVisitor.register(objectName, mBeanInfo);
        // END SNIPPET: jmxvisitor
        
        // START SNIPPET: register
        MutablePicoContainer picoContainer = new DefaultPicoContainer();
        MBeanServer mBeanServer = MBeanServerFactory.createMBeanServer();
        picoContainer.registerComponentInstance(MBeanServer.class, mBeanServer);
        
        picoContainer.registerComponentInstance(SampleService.class, new SampleService());
        picoContainer.accept(jmxVisitor);
        // END SNIPPET: register        
    }
}

