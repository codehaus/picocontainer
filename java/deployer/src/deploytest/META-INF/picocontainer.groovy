class MyContainerBuilder {
    buildContainer(parentX) {
        pico = new org.picocontainer.defaults.DefaultPicoContainer(parentX)
        pico.registerComponentInstance("Groovy")
        pico.registerComponentImplementation("zap", foo.bar.Zap)
        return pico
    }
}
