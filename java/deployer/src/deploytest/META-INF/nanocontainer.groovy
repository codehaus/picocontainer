builder = new org.nanocontainer.script.groovy.NanoGroovyBuilder()
pico = builder.container(parent:parent) {
 component(instance:'Groovy')
 component(key:'zap', class:foo.bar.Zap)
}
