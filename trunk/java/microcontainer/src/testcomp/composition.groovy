import org.nanocontainer.script.groovy.NanoContainerBuilder
import org.microcontainer.jmx.JmxDecorationDelegate
import javax.management.MBeanServerFactory
import java.io.File

hiddenJarPath = new File(parent.getComponentInstance("workingDir"), "/MCA-INF/hidden/hidden.jar").getCanonicalPath()

pico = builder.container(parent:parent) {

	classpathelement(path:hiddenJarPath);
	// the two keys should already be in the classpath (classloader tree)
	// the two impls should be invisible to this script,
	// and thus mentioned by name (via the manually added hidden jar above).
	component(key:org.microcontainer.test.TestComp, class:"org.microcontainer.test.hopefullyhidden.TestCompImpl")
	component(key:org.microcontainer.testapi.TestPromotable, class:"org.microcontainer.test.hopefullyhidden.TestPromotableImpl")

    // register MBeanServer and DynamicMBeanFactory ... todo this should be hidden
    component(key:javax.management.MBeanServer, instance:MBeanServerFactory.newMBeanServer())
    component(key:org.nanocontainer.jmx.DynamicMBeanFactory, class:org.nanocontainer.jmx.mx4j.MX4JDynamicMBeanFactory)

    component(key:'wilma', class:'org.nanocontainer.testmodel.WilmaImpl') {
    	jmx(key:'domain:wilma=default', operations:['helloCalled'], description:'This is the wilma mbeanInfo escription' )
    }
}

