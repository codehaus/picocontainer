package org.nanocontainer.boot;

import java.net.*;
import java.io.*;
import java.lang.reflect.*;


public class NanoContainerBooter {

  public static void main(String[] args) throws Exception {
  
    URLClassLoader baseClassLoader = new URLClassLoader( 
      new URL[] {
        new File("lib/picocontainer-1.2-beta-1.jar").toURL()
      },
      NanoContainerBooter.class.getClassLoader().getParent()
    );

    URLClassLoader hiddenClassLoader = new URLClassLoader(
      new URL[] {
        new File("lib/asm-1.5.2.jar").toURL(),
        new File("lib/asm-util-1.5.2.jar").toURL(),
        new File("lib/commons-cli-1.0.jar").toURL(),
        new File("lib/groovy-1.0-jsr-01.jar").toURL(),
        new File("lib/nanocontainer-1.0-RC-1.jar").toURL(),
        new File("lib/antlr-2.7.5.jar").toURL()
      },
      baseClassLoader
    );

    Class nanoStandalone = hiddenClassLoader.loadClass("org.nanocontainer.Standalone");
    Constructor ctor = nanoStandalone.getConstructors()[0];
    System.out.println("NanoContainer-Boot: Booting.");
    ctor.newInstance(new Object[] { args });
    System.out.println("NanoContainer-Boot: Booted.");

  }

}