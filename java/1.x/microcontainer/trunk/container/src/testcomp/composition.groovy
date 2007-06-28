import org.nanocontainer.script.groovy.OldGroovyNodeBuilder
import org.nanocontainer.testmodel.Wilma
import org.microcontainer.jmx.JmxDecorationDelegate
import javax.management.MBeanServerFactory
import java.io.File

hiddenJarPath = new File(parent.getComponentInstance("workingDir"), "/MCA-INF/hidden/hidden.jar").getCanonicalPath()

pico = builder.container(parent:parent) {

	classPathElement(path:hiddenJarPath);
	// the two keys should already be in the classpath (classloader tree)
	// the two impls should be invisible to this script,
	// and thus mentioned by name (via the manually added hidden jar above).
	component(key:org.microcontainer.test.TestComp, class:"org.microcontainer.test.hopefullyhidden.TestCompImpl")
	component(key:org.microcontainer.testapi.TestPromotable, class:"org.microcontainer.test.hopefullyhidden.TestPromotableImpl")

    // register MBeanServer ... TODO: this should be hidden
    component(key:javax.management.MBeanServer, instance:MBeanServerFactory.newMBeanServer())

    component(key:'wilma', class:'org.nanocontainer.testmodel.WilmaImpl') {
    	jmx(key:'domain:wilma=default', management:Wilma,  operations:['helloCalled'], description:'This is the wilma MBeanInfo description' )
    }
    component(key:'wilmaDouble', class:'org.nanocontainer.testmodel.WilmaImpl') {
    	jmx(key:'domain:wilma=double', operations:['helloCalled'], description:'This is the wilma ModelMBeanInfo description' )
    }
}

