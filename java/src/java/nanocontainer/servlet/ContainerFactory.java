package nanocontainer.servlet;



import picocontainer.PicoContainer;



public interface ContainerFactory {



    PicoContainer buildContainer(String configName);



    PicoContainer buildContainerWithParent(PicoContainer parentContainer, String configName);



    ObjectInstantiater buildInstantiater(PicoContainer parentContainer);



    void destroyContainer(PicoContainer container);



}

