import org.nanocontainer.script.groovy.NanoContainerBuilder
import org.microcontainer.ResidualController
import org.microcontainer.impl.MicroBeanShellConsole
import org.microcontainer.impl.DefaultConfiguration
import org.microcontainer.impl.DefaultMcaDeployer
import org.microcontainer.impl.GroovyDeploymentScriptHandler
import org.microcontainer.impl.DefaultClassLoaderFactory
import org.microcontainer.impl.DefaultKernel
import org.microcontainer.jmx.JmxDecorationDelegate
import javax.management.MBeanServer
import javax.management.MBeanServerFactory

// Script for boot MicroContainer with JMX support

builder = new NanoContainerBuilder(new JmxDecorationDelegate())

nano = builder.container(parent:parent) {
  	classpathelement(path:"lib/hidden/microcontainer-impl-0.1-SNAPSHOT.jar");
  	component(key:MBeanServer, instance:MBeanServerFactory.createMBeanServer("microcontainer"))
	component(class:MicroBeanShellConsole)
	component(class:DefaultConfiguration)
    component(class:DefaultClassLoaderFactory)
    component(class:DefaultMcaDeployer)
    component(class:GroovyDeploymentScriptHandler)

	// this must be at the end of the script
	component(class:org.microcontainer.impl.DefaultKernel) {
  		jmx(key:'microcontainer:kernel=default', operations:['size'])
    }
}

pico = nano.getPico();
residualController = new ResidualController(pico);
residualController.start();