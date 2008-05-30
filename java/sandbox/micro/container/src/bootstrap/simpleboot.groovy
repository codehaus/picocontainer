import org.nanocontainer.script.groovy.NanoContainerBuilder

import org.microcontainer.impl.MicroBeanShellConsole
import org.microcontainer.impl.DefaultConfiguration
import org.microcontainer.impl.DefaultMcaDeployer
import org.microcontainer.impl.GroovyDeploymentScriptHandler
import org.microcontainer.impl.DefaultClassLoaderFactory
import org.microcontainer.impl.DefaultKernel

import org.microcontainer.ResidualController

// Script to boot MicroContainer

builder = new NanoContainerBuilder()

nano = builder.container(parent:parent) {
	classpathelement(path:"microcontainer-impl-0.1-SNAPSHOT.jar");

	component(class:MicroBeanShellConsole)
	component(class:DefaultConfiguration)
	component(class:DefaultClassLoaderFactory)
	component(class:DefaultMcaDeployer)
	component(class:GroovyDeploymentScriptHandler)
	component(class:DefaultKernel)
}

pico = nano.getPico();
residualController = new ResidualController(pico);
residualController.start();