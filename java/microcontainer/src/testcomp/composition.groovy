//import org.nanocontainer.script.groovy.NanoContainerBuilder
import org.microcontainer.MicroGroovyBuilder
import javax.management.MBeanServerFactory
import javax.management.MBeanOperationInfo
import java.io.File

hiddenJarPath = new File(parent.getComponentInstance("workingDir"), "/MCA-INF/hidden/hidden.jar").getCanonicalPath()

pico = new MicroGroovyBuilder().container(parent:parent) {

	classpathelement(path:hiddenJarPath);
	// the two keys should already be in the classpath (classloader tree)
	// the two impls should be invisible to this script,
	// and thus mentioned by name (via the manually added hidden jar above).
	component(key:org.microcontainer.test.TestComp, class:"org.microcontainer.test.hopefullyhidden.TestCompImpl")
	component(key:org.microcontainer.testapi.TestPromotable, class:"org.microcontainer.test.hopefullyhidden.TestPromotableImpl")

    // register MBeanServer... todo this should be hidden
    component(key:javax.management.MBeanServer, instance:MBeanServerFactory.newMBeanServer())

    jmx(key:'microcontainer:kernel=default', methods:['put', 'size']) {
      component(key:java.util.Map, class:java.util.HashMap)
    }



    // set up MBean*Info for JMX
    //info = new MBeanOperationInfo("size", "return the number of components", null, "int", MBeanOperationInfo.INFO)
    //jmx(key:'microcontainer:kernel=default', class:'java.util.HashMap', operations:[info])
}

