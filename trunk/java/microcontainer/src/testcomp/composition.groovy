import org.nanocontainer.script.groovy.NanoGroovyBuilder
import java.io.File

builder = new NanoGroovyBuilder()
hiddenJarPath = new File(parent.getComponentInstance("workingDir"), "/MCA-INF/hidden/hidden.jar").getCanonicalPath()

builder.container(parent:parent) {

	classpathelement(path:hiddenJarPath);
	// the two keys should already be in the classpath (classloader tree)
	// the two impls should be invisible to this script,
	// and thus mentioned by name (via the manually added hidden jar above).
	component(key:"org.microcontainer.test.TestComp", class:"org.microcontainer.test.hopefullyhidden.TestCompImpl")
	component(key:"org.microcontainer.testapi.TestPromotable", class:"org.microcontainer.test.hopefullyhidden.TestPromotableImpl")
}

