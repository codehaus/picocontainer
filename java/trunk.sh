echo ** PicoContainer
cd picocontainer/trunk
maven multiproject:install
cd -
echo ** NanoContainer
cd nanocontainer/trunk
maven -o multiproject:install
cd -
echo ** NanoContainer - NanoWar
cd nanocontainer-nanowar/trunk
maven -o multiproject:install
cd -
echo ** NanoContainer - NanoPersistence
cd nanocontainer-persistence/trunk
maven -o multiproject:install
cd -
echo ** NanoContainer - NanoRemoting
cd nanocontainer-remoting/trunk
maven -o multiproject:install
cd -
echo ** NanoContainer - NanoIoC
cd nanocontainer-ioc/trunk
maven -o multiproject:install
cd -
