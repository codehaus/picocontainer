package nanocontainer.servlet;

import picocontainer.Container;

public interface ContainerFactory {

    Container buildContainer(String configName);

    Container buildContainerWithParent(Container parentContainer, String configName);

    void destroyContainer(Container container);

}








  