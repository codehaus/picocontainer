import org.nanocontainer.script.groovy.NanoContainerBuilder
import org.microcontainer.jmx.JmxDecorationDelegate
import javax.management.MBeanServerFactory

builder = new NanoContainerBuilder(new JmxDecorationDelegate())

nano = builder.container(parent:parent) {
  	classpathelement(path:"lib/hidden/microcontainer-impl-0.1-SNAPSHOT.jar");
	component(key:org.microcontainer.McaDeployer, class:"org.microcontainer.impl.DefaultMcaDeployer")
	component(class:"org.microcontainer.impl.MicroBeanShellConsole")

	component(key:javax.management.MBeanServer, instance:MBeanServerFactory.createMBeanServer("microcontainer"))

  	component(key:org.microcontainer.Kernel, class:org.microcontainer.impl.DefaultKernel) {
  		jmx(key:'microcontainer:kernel=default', operations:['size'])
    }
}

pico = nano.getPico();
org.microcontainer.ResidualController rc = new org.microcontainer.ResidualController(pico);
rc.start();