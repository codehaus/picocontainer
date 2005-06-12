package org.nanocontainer.boot;

import java.io.File;
import java.io.IOException;
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

    public NanoContainerBooter(String[] args) throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, InstantiationException, IOException {

        URL picoURL = null;

        File lib = new File("lib");
        File[] libs = lib.listFiles();
        for (int i = 0; i < libs.length; i++) {
            File file = libs[i];
            URL fileURL = file.toURL();
            ClassLoader classLoader = new URLClassLoader(new URL[]{fileURL}, NanoContainerBooter.class.getClassLoader());

            checkForClass(classLoader, fileURL, "org.apache.commons.cli.CommandLine", 0, "Commons CLI");
            checkForClass(classLoader, fileURL, "org.nanocontainer.NanoContainer", 1, "NanoContainer");
            checkForClass(classLoader, fileURL, "groovy.lang.Range", 2, "Groovy");
            checkForClass(classLoader, fileURL, "org.objectweb.asm.ClassVisitor", 3, "ASM");
            checkForClass(classLoader, fileURL, "org.objectweb.asm.util.PrintClassVisitor", 4, "ASM Util");
            checkForClass(classLoader, fileURL, "antlr.CharFormatter", 5, "Antlr");

            if (file.getName().startsWith("picocontainer")) {
                if (picoURL != null) {
                    System.err.println("There is more than one jar in the lib dir that contains PicoContainer classes");
                    System.exit(10);
                }
                picoURL = fileURL;

            }
        }

        errorIfNull(picoURL, "PicoContainer");

        URLClassLoader baseClassLoader = new URLClassLoader(
                        new URL[]{picoURL},
                        NanoContainerBooter.class.getClassLoader().getParent()
                );

        errorIfNull(urls[0], "Commons CLI");
        errorIfNull(urls[1], "NanoContainer");
        errorIfNull(urls[2], "Groovy");
        errorIfNull(urls[3], "ASM");
        errorIfNull(urls[4], "ASM Util");
        errorIfNull(urls[5], "Antlr");

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

    private void errorIfNull(URL url, String msg) {
        if (url == null) {
            System.err.println(msg + " jar missing from lib directory");
            System.exit(10);
        }

    }

    private void checkForClass(ClassLoader classLoader, URL url, String className, int ix, String type) throws ClassNotFoundException {
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