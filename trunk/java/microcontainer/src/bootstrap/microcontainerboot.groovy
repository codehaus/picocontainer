import org.nanocontainer.script.groovy.NanoContainerBuilder
import org.picocontainer.defaults.ComponentParameter
import org.microcontainer.jmx.JmxDecorationDelegate
import org.microcontainer.McaDeployer
import org.microcontainer.impl.DefaultMcaDeployer
import org.microcontainer.DeploymentScriptHandler
import org.microcontainer.impl.GroovyDeploymentScriptHandler
import org.microcontainer.ClassLoaderFactory
import org.microcontainer.impl.DefaultClassLoaderFactory
import javax.management.MBeanServerFactory
import java.io.File

builder = new NanoContainerBuilder(new JmxDecorationDelegate())

nano = builder.container(parent:parent) {
  	classpathelement(path:"lib/hidden/microcontainer-impl-0.1-SNAPSHOT.jar");
	component(class:org.microcontainer.impl.MicroBeanShellConsole)
	component(key:javax.management.MBeanServer, instance:MBeanServerFactory.createMBeanServer("microcontainer"))

    workDir = new File("work") // microcontainer work directory
    tempDir = new File("temp") // microcontainer temp directory (for expanding *.mca files)

    component(key:"work.dir", instance:workDir);
	component(key:"temp.dir", instance:tempDir);

    component(key:ClassLoaderFactory, class:DefaultClassLoaderFactory, parameters:[new ComponentParameter("work.dir")]);

    parms = [ new ComponentParameter("work.dir"), new ComponentParameter("temp.dir") ]
    component(key:McaDeployer, class:DefaultMcaDeployer, parameters:parms);

    parms = [ ComponentParameter.DEFAULT, new ComponentParameter("work.dir") ]
	component(key:DeploymentScriptHandler, class:GroovyDeploymentScriptHandler, parameters:parms);

	// this must be at the end of the script
	component(key:org.microcontainer.Kernel, class:org.microcontainer.impl.DefaultKernel) {
  		jmx(key:'microcontainer:kernel=default', operations:['size'])
    }
}

pico = nano.getPico();
org.microcontainer.ResidualController rc = new org.microcontainer.ResidualController(pico);
rc.start();