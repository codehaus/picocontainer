In order to build pico/nano for JDK 1.3, perform the following steps:

1) Uncomment lines around line 100 in project.xml
2) Define your JAVA_HOME to point to a JDK 1.3

Then run:

maven -Djvm.version=1.3

from this folder. It won't get you all the way through, but it will get you through picocontainer and nanocontainer core at least.