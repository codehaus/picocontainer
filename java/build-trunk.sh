echo \*\* Building PicoContainer, NanoContainer and Microcontainer trunks
echo \*\* PicoContainer
cd picocontainer/trunk
maven multiproject:install
cd -
echo \*\* NanoContainer
cd nanocontainer/trunk
maven multiproject:install
cd -
echo \*\* NanoContainer - NanoWar
cd nanocontainer-nanowar/trunk
maven multiproject:install
cd -
echo \*\* NanoContainer - Persistence
cd nanocontainer-persistence/trunk
maven multiproject:install
cd -
echo \*\* NanoContainer - Remoting
cd nanocontainer-remoting/trunk
maven multiproject:install
cd -
echo \*\* NanoContainer - IoC
cd nanocontainer-ioc/trunk
maven multiproject:install
cd -
echo \*\* NanoContainer - Tools
cd nanocontainer-tools/trunk
maven multiproject:install
cd -
echo \*\* NanoContainer - Sandbox
cd nanocontainer-sandbox/trunk
maven multiproject:install
cd -
echo \*\* MicroContainer
cd microcontainer/trunk
maven multiproject:install
cd -
echo \*\* Demos
cd demos/trunk
maven multiproject:install
cd -
