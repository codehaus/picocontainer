// No need to extend anything, but we must have a method with this signature
class MyContainerBuilder {
    buildContainer(parent, assemblyScope) {
        pico = new org.picocontainer.defaults.DefaultPicoContainer(parent)
        pico.registerComponentInstance("Groovy")
        pico.registerComponentImplementation("zap", foo.bar.Zap)
        return pico
    }
}
