package org.nanocontainer.nanoweb;

import groovy.lang.GroovyClassLoader;
import org.codehaus.groovy.syntax.SyntaxException;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Loads classes from scripts and caches them based on the URL's
 * timestamp.
 * @author Aslak Helles&oslash;y
 * @author Kouhei Mori
 * @version $Revision$
 */
public class CachingScriptClassLoader {
    private final Map scriptLoadTimestamps = Collections.synchronizedMap(new HashMap());
    private final Map scriptClasses = Collections.synchronizedMap(new HashMap());
    private static final Long NEVER = new Long(Long.MIN_VALUE);

    public Class getClass(URL scriptURL) throws IOException, ScriptException {
        // Find the timestamp of the scriptURL
        String urlAsString = scriptURL.toExternalForm();
        URLConnection urlConnection = scriptURL.openConnection();
        Long lastLoaded = (Long) scriptLoadTimestamps.get(urlAsString);
        if(lastLoaded == null) {
            lastLoaded = NEVER;
        }

        // Reload the class or reuse the cached class if the scriptURL hasn't changed.
        Class scriptClass;
        if(lastLoaded.longValue() < urlConnection.getLastModified()) {
            scriptClass = loadAndCache(scriptURL, urlConnection);
        } else {
            scriptClass = (Class) scriptClasses.get(urlAsString);
        }
        return scriptClass;
    }

    private Class loadAndCache(URL scriptURL, URLConnection urlConnection) throws IOException, ScriptException {
        Class scriptClass = loadGroovyClass(urlConnection, scriptURL);
        String urlAsString = scriptURL.toExternalForm();
        scriptClasses.put(urlAsString, scriptClass);
        long lastChanged = urlConnection.getLastModified();
        scriptLoadTimestamps.put(urlAsString, new Long(lastChanged));
        return scriptClass;
    }

    // May be factored out to a separate strategy later if we decide to support
    // other languages than Groovy
    private Class loadGroovyClass(URLConnection urlConnection, URL scriptURL) throws IOException, ScriptException {
        GroovyClassLoader loader = new GroovyClassLoader(getClass().getClassLoader());
        try {
            Class scriptClass = loader.parseClass(urlConnection.getInputStream(), scriptURL.getFile());
            return scriptClass;
        } catch (SyntaxException e) {
            throw new ScriptException(scriptURL, e);
        }
    }
}