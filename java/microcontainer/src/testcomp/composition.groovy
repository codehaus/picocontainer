import org.nanocontainer.script.groovy.NanoGroovyBuilder
import org.picocontainer.PicoContainer
import org.microcontainer.DeploymentScript

class ContainerScript implements DeploymentScript {

    public PicoContainer build() {
        child = null
        builder = new NanoGroovyBuilder()

        builder.container {
             addClassPath("MAR-INF/hidden/hidden.jar");
             // the two keys should already be in the classpath (classloader tree)
             // the two impls should be invisible to this script, 
             // and thus mentioned by name (via the manually added hidden jar above).
             component(key:org.microcontainer.test.TestComp, class:"org.microcontainer.test.hopefullyhidden.TestCompImpl")
             component(key:org.microcontainer.testapi.TestPromotable, class:"org.microcontainer.test.hopefullyhidden.TestPromotableImpl")
        }

        return child
    }
}

