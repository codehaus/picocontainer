import org.nanocontainer.script.groovy.NanoGroovyBuilder
import org.picocontainer.PicoContainer
import org.microcontainer.DeploymentScript

class ContainerScript implements DeploymentScript {

	public PicoContainer build() {
		child = null
		builder = new NanoGroovyBuilder()

		builder.container {
			child = container() {
				component(key:"org.microcontainer.test.TestComp", class:org.microcontainer.test.hopefullyhidden.TestCompImpl)
				component(key:"org.microcontainer.testapi.TestPromotable", class:org.microcontainer.test.hopefullyhidden.TestPromotableImpl)
    		}
		}

		return child
	}
}

