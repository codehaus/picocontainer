package org.nanocontainer.nanoweb;

import org.codehaus.groovy.syntax.SyntaxException;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

import groovy.lang.GroovyClassLoader;

/**
 * Loads classes from scripts and caches them based on the URL's
 * timestamp.
 * @author Aslak Helles&oslash;y
 * @author Kouhei Mori
 * @version $Revision$
 */
public class CachingScriptClassLoader {
    private final Map groovyActionLoadTimestamps = Collections.synchronizedMap(new HashMap());
    private final Map scriptClasses = Collections.synchronizedMap(new HashMap());

    public Class getClass(URL scriptURL) throws SyntaxException, IOException {
        String urlAsString = scriptURL.toExternalForm();
        Class scriptClass = (Class) scriptClasses.get(urlAsString);
        URLConnection urlConnection = scriptURL.openConnection();
        if(scriptClass == null) {
            scriptClass = loadGroovyClass(urlConnection, scriptURL);
            cache(scriptURL, urlConnection, scriptClass);
        } else {
            Long lastLoaded = (Long) groovyActionLoadTimestamps.get(urlAsString);
            if(lastLoaded.longValue() < urlConnection.getLastModified()) {
                scriptClass = loadGroovyClass(urlConnection, scriptURL);
                cache(scriptURL, urlConnection, scriptClass);
            }
        }
        return scriptClass;
    }

    private Class loadGroovyClass(URLConnection urlConnection, URL scriptURL) throws SyntaxException, IOException {
        GroovyClassLoader loader = new GroovyClassLoader(getClass().getClassLoader());
        Class scriptClass = loader.parseClass(urlConnection.getInputStream(), scriptURL.getFile());
        return scriptClass;
    }

    private void cache(URL scriptURL, URLConnection urlConnection, Class scriptClass) {
        String urlAsString = scriptURL.toExternalForm();
        scriptClasses.put(urlAsString, scriptClass);
        long lastChanged = urlConnection.getLastModified();
        groovyActionLoadTimestamps.put(urlAsString, new Long(lastChanged));
    }
}