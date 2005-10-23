echo ** Pico
cd picocontainer/trunk/container/
maven jar:install
cd -
echo ** TCK
cd picocontainer/trunk/tck
maven -o jar:install
cd -
echo ** Gems
cd picocontainer/trunk/gems/ 
maven -o jar:install
cd -
echo ** nano testmodel
cd nanocontainer/trunk/testmodel/ 
maven -o jar:install
cd -
echo ** nanocontainer
cd nanocontainer/trunk/container/ 
maven -o jar:install
cd -
echo ** booter
cd nanocontainer/trunk/booter/ 
maven -o jar:install
cd -
echo ** deployer
cd nanocontainer/trunk/deployer/ 
maven -o jar:install
cd -
echo ** nanowar
cd nanocontainer-nanowar/trunk/nanowar
maven -o jar:install
cd -
echo ** nanowar-sample
cd nanocontainer-nanowar/trunk/nanowar-sample
maven -o jar:install
cd -
