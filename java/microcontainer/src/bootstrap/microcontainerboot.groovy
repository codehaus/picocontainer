import org.nanocontainer.script.groovy.NanoGroovyBuilder

builder = new NanoGroovyBuilder()

builder.container(parent:parent) {
  classpathelement(path:"lib/hidden/microcontainer-impl-0.1-SNAPSHOT.jar");
  component(key:org.microcontainer.Kernel, class:"org.microcontainer.impl.DefaultKernel")
  component(class:"org.microcontainer.impl.ShutdownPreventer")  
  component(key:org.microcontainer.McaDeployer, class:"org.microcontainer.impl.DefaultMcaDeployer")  
}
