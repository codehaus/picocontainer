import org.nanocontainer.script.groovy.NanoContainerBuilder

builder = new NanoGroovyBuilder()

pico = builder.container(parent:parent,class:ImplementationHidingSoftCompositionPicoContainer) {
  classpathelement(path:"lib/hidden/microcontainer-impl-0.1-SNAPSHOT.jar");
  component(key:org.microcontainer.Kernel, class:"org.microcontainer.impl.DefaultKernel")
  component(key:org.microcontainer.McaDeployer, class:"org.microcontainer.impl.DefaultMcaDeployer")  
  component(class:"org.microcontainer.impl.MicroBeanShellConsole")
}
pico.start();
org.microcontainer.ResidualController rc = new org.microcontainer.ResidualController(pico);
rc.start();

