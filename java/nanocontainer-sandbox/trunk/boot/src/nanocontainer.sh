#!/bin/bash

EXEC="$JAVA_HOME/bin/java -Djava.security.manager -Djava.security.policy=file:lib/boot.policy -classpath lib/nanocontainer-boot-1.0-SNAPSHOT.jar  org.nanocontainer.boot.NanoContainerBooter -c composition.groovy"
echo $EXEC
$EXEC

