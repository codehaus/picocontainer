PROJECT: NanoContainer Remoting
====================================================================

1. DESCRIPTION: 
---------------

This directory contains the NanoContainer Remoting sources and all resources required to build libraries.
Structure and build procedures are described below.

2. STRUCTURE:
-------------

remoting
    |
    +-- common               	 common interfaces and command objects marshalled across the wire
                                 client API interfaces and classes
                                 server API interfaces and classes
    +-- client                   client implementation
    +-- server                   server implementation
    +-- tools			         Tools like Proxy Generator, Ant tasks, JavaCompiler
    +-- integrationtests	     Integration test cases

Each module comes with its own unit tests as well as module specific documentation.

3. BUILD PROCEDURE :
--------------------

   3.1 BUILD PROCEDURE USING MAVEN :
------------------------------------

  $ maven 

 To clean up all the mess use 'clean-all' goal

  $ maven clean-all


FURTHER  QUERIES :
------------------
For additional information please check the documentation and also use the 
PicoContainer Mailing list (subscription details in the doc).

Finally, please keep in mind that while NanoContainer Remoting is nearing completion and
readiness for a first release, it should be considered as beta software as 
APIs are changing, and documentation is evolving.

vinayc 27 August 2003
paul   25 Sept 2005

