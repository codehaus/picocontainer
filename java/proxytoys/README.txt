This code replaces the code described in a mail to dev@picocontainer.codehaus.org on May 17 2004:

---------------------------%<---------------------------
Folks,

Dan and I have factored out a lot of the dynamic proxy related code that is not strictly tied to picocontainer/nanocontainer. It now lives in a small project on Codehaus called proxytoys. (This project initially grew out of http://xjb.codehaus.org/). There is currently no online docs for proxytoys (will be really soon!), but you can get a quick idea of what it does by looking at:

svn checkout svn://svn.proxytoys.codehaus.org/proxytoys/scm/trunk/proxytoys

In short proxytoys is:
o A core library that lets you create dynamic proxies (implemented with standard proxies or Cglib) using the same API.
o Various factory classes for creation of proxy objects that are:
 - hotswappable/implementation hiding
 - multicasting
 - nullobject
 - delegating/decorating
 - failover

The proxytoys library doesn't have any external dependencies exept an optional dependency on Cglib if you want to use that.

Does anyone object if I refactor the following PicoContainer/NanoContainer classes to depend on proxytoys? (I am suggesting moving all of the classes below to org.nanocontainer.proxytoys)

o org.picocontainer.alternatives.ImplementationHidingComponentAdapter (very useful, but not strictly related to the pico core)
o org.picocontainer.defaults.ImplementationHidingComponentAdapterFactory
o org.nanocontainer.multicast.* (our pluggable lifecycle support - will be greatly simplified)
o org.nanocontainer.remoting.*

Aslak
---------------------------%<---------------------------

There were no objections to that....
