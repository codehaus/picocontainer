container = new org.picocontainer.defaults.ImplementationHidingPicoContainer()

// assume all /promoted jars are in the current classloader tree to *this*

apiClassLoader = // TODO. err turn /MAR-INF/components/* and /MAR-INF/lib/* jars in to classloader via URL I guess
hiddenClassLoader = // TODO. err turn /MAR-INF/hidden/* jars in to classloader via URL I guess (with apiClassLoader as parent)

reflectionContainerAdapter = new DefaultReflectionContainerAdapter(hiddenClassLoader, container);
reflectionContainerAdapter.registerComponentImplementation("org.megacontainer.test.TestComp", "org.megacontainer.test.hopefullyhidden.TestCompImpl");
reflectionContainerAdapter.registerComponentImplementation("org.megacontainer.testapi.TestPromotable", "org.megacontainer.test.hopefullyhidden.TestPromotableImpl");

// note the reflectionContainerAdapter gets GC'd after this, as does the GroovyContainerBuilder building this
// TODO .... move to *true* Groovy builder style

// start()ing will happen after this script is executed of course