import org.nanocontainer.script.groovy.NanoContainerBuilder
import org.microcontainer.jmx.JmxDecorationDelegate

nano = builder.container(parent:parent) {
	component(key:'echo', class:"org.microcontainer.example.EchoServer")
}
