package org.nanocontainer.boot;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.ArrayList;

public class NanoContainerBooter {

    private List commonURLs = new ArrayList();
    private List hiddenURLs = new ArrayList();

    public static void main(String[] args) throws Exception {
        new NanoContainerBooter(args);
    }

    public NanoContainerBooter(String[] args) throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, InstantiationException, IOException {

        File[] libs = new File("lib/common").listFiles();
        for (int i = 0; i < libs.length; i++) {
            File file = libs[i];
            URL fileURL = file.toURL();
            commonURLs.add(fileURL);
        }

        libs = new File("lib/hidden").listFiles();
        for (int i = 0; i < libs.length; i++) {
            File file = libs[i];
            URL fileURL = file.toURL();
            hiddenURLs.add(fileURL);
        }

        URL[] common = new URL[commonURLs.size()];
        commonURLs.toArray(common);

        URLClassLoader baseClassLoader = new URLClassLoader(common,
                        NanoContainerBooter.class.getClassLoader().getParent() );

        URL[] hidden = new URL[hiddenURLs.size()];
        hiddenURLs.toArray(hidden);

        URLClassLoader hiddenClassLoader = new URLClassLoader(
                        hidden, baseClassLoader );

        Class nanoStandalone = hiddenClassLoader.loadClass("org.nanocontainer.Standalone");
        Constructor ctor = nanoStandalone.getConstructors()[0];
        System.out.println("NanoContainer-Boot: Booting.");
        ctor.newInstance(new Object[]{args});
        System.out.println("NanoContainer-Boot: Booted.");

    }

}