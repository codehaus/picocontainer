This code is original copyright by J Tec Team 2004-2005.
Original Author is Konstantin Pribluda.

This code is donated  to pico/nanocontainer organisation
to be distributed and used under terms of pico/nanocontainer 
license.

This demo needs no aditional configuartion, and war archive 
shall run on any servlet container. Have fun. 

About J Tec Team:
We are small software development company located in Wiesbaden,
Germany. You can reach us under http://www.j-tec-team.de/

BUILD NOTES:
Current version of jobdemo needs an up-to-date velocity version 
1.5, that is not avaiable in public yet. Therefore we provide
a snapshot from CVS in lib.

Unfortunately Maven 1.0.2 has currently a bug using the 
override mechanism on libraries that are also used internally
by a plugin. Therefore this project cannot be build from the
current location. You must build from the Pico/Nano root 
directory one level above using:

maven -Dmaven.multiproject.includes=jobdemo/project.xml multiproject:install

