import org.nanocontainer.script.groovy.NanoContainerBuilder
import org.microcontainer.jmx.JmxDecorationDelegate

nano = new NanoContainerBuilder(new JmxDecorationDelegate()).container(parent:parent) {
	component(key:'echo', class:"org.microcontainer.example.EchoServer")
}
