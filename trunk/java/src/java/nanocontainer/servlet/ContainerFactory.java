package nanocontainer.servlet;

import picocontainer.Container;

public interface ContainerFactory {

    Container buildContainer(String configName);

    Container buildContainerWithParent(Container parentContainer, String configName);

    ObjectInstantiater buildInstantiater(Container parentContainer);

    void destroyContainer(Container container);

}
