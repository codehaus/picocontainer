echo ** Building PicoContainer and NanoContainer trunks
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
echo ** NanoContainer - Persistence
cd nanocontainer-persistence/trunk
maven -o multiproject:install
cd -
echo ** NanoContainer - Remoting
cd nanocontainer-remoting/trunk
maven -o multiproject:install
cd -
echo ** NanoContainer - IoC
cd nanocontainer-ioc/trunk
maven -o multiproject:install
cd -
