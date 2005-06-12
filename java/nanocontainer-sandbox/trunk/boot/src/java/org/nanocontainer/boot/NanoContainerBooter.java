package org.nanocontainer.boot;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.MalformedURLException;

public class NanoContainerBooter {

    private URL[] urls = new URL[6];

    public static void main(String[] args) throws Exception {
        new NanoContainerBooter(args);
    }

    public NanoContainerBooter(String[] args) throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, InstantiationException, MalformedURLException {

        URLClassLoader baseClassLoader = new URLClassLoader(
                        new URL[]{
                                    new File("lib/picocontainer-1.2-beta-1.jar").toURL()
                            },
                        NanoContainerBooter.class.getClassLoader().getParent()
                );

        File lib = new File("lib");
        File[] libs = lib.listFiles();
        for (int i = 0; i < libs.length; i++) {
            File file = libs[i];
            URL fileURL = file.toURL();
            ClassLoader classLoader = new URLClassLoader(new URL[]{fileURL}, NanoContainerBooter.class.getClassLoader());

            look(classLoader, fileURL, "org.apache.commons.cli.CommandLine", 0, "ASM");
            look(classLoader, fileURL, "org.nanocontainer.NanoContainer", 1, "ASM");
            look(classLoader, fileURL, "groovy.lang.Range", 2, "ASM");
            look(classLoader, fileURL, "org.objectweb.asm.ClassVisitor", 3, "ASM");
            look(classLoader, fileURL, "org.objectweb.asm.util.PrintClassVisitor", 4, "ASM");
            look(classLoader, fileURL, "antlr.CharFormatter", 5, "ASM");
        }

        err(urls[0], "Commons CLI");
        err(urls[1], "NanoContainer");
        err(urls[2], "Groovy");
        err(urls[3], "ASM");
        err(urls[4], "ASM Util");
        err(urls[5], "Antlr");

        URLClassLoader hiddenClassLoader = new URLClassLoader(
                        urls,
                        baseClassLoader
                );

        Class nanoStandalone = hiddenClassLoader.loadClass("org.nanocontainer.Standalone");
        Constructor ctor = nanoStandalone.getConstructors()[0];
        System.out.println("NanoContainer-Boot: Booting.");
        ctor.newInstance(new Object[]{args});
        System.out.println("NanoContainer-Boot: Booted.");

    }

    private void err(URL url, String msg) {
        if (url == null) {
            System.err.println(msg + " jar missing from lib directory");
            System.exit(10);
        }

    }

    private void look(ClassLoader classLoader, URL url, String className, int ix, String type) throws ClassNotFoundException {
        boolean classFound = false;
        try {
            classLoader.loadClass(className);
            classFound = true;
        } catch (ClassNotFoundException e) {
        } catch (NoClassDefFoundError e) {
            classFound = true;
        }
        if (classFound) {
            if (urls[ix] != null) {
                System.err.println("There is more than one jar in the lib dir that contains " + type + " classes");
                System.exit(10);
            }
            urls[ix] = url;
        }
    }
}