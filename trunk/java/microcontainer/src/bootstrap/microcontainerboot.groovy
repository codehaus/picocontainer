import org.nanocontainer.script.groovy.NanoContainerBuilder

builder = new NanoContainerBuilder()

nano = builder.container(parent:parent, class:"org.picocontainer.defaults.DefaultPicoContainer") {
  classpathelement(path:"lib/hidden/microcontainer-impl-0.1-SNAPSHOT.jar");
  component(key:org.microcontainer.Kernel, class:"org.microcontainer.impl.DefaultKernel")
  component(key:org.microcontainer.McaDeployer, class:"org.microcontainer.impl.DefaultMcaDeployer")
  component(class:"org.microcontainer.impl.MicroBeanShellConsole")
}
pico = nano.getPico();
org.microcontainer.ResidualController rc = new org.microcontainer.ResidualController(pico);
rc.start();