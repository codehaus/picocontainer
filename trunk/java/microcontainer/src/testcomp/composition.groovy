import org.nanocontainer.reflection.DefaultReflectionContainerAdapter;

// must be named "pico" (required by GroovyContainerBuilder)
pico = new org.picocontainer.defaults.ImplementationHidingPicoContainer()

// assume all /promoted jars are in the current classloader tree to *this*

hiddenClassLoader = parent.getComponentInstance("hiddenClassLoader")
reflectionContainerAdapter = new DefaultReflectionContainerAdapter(hiddenClassLoader, pico);
reflectionContainerAdapter.registerComponentImplementation("org.microcontainer.test.TestComp", "org.microcontainer.test.hopefullyhidden.TestCompImpl");
reflectionContainerAdapter.registerComponentImplementation("org.microcontainer.testapi.TestPromotable", "org.microcontainer.test.impl.TestPromotableImpl");

// note the reflectionContainerAdapter gets GC'd after this, as does the GroovyContainerBuilder building this
// TODO .... move to *true* Groovy builder style

// start()ing will happen after this script is executed of course