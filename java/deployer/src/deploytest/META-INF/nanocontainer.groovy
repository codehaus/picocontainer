pico = new org.picocontainer.defaults.DefaultPicoContainer(parent)
pico.registerComponentInstance("Groovy")
pico.registerComponentImplementation("zap", foo.bar.Zap)
