package nanocontainer;

import java.util.StringTokenizer;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URI;
import java.net.URL;

/**
 *
 * @author Aslak Hellesoy
 * @version $Revision$
 */
public class FileUtils {
    /**
     * Returns the root directory of the package hierarchy where this class is
     * located. This will either be a directory or a jar file.
     *
     * @return the root directory or jar file where the class is located.
     */
    public static File getRoot(Class clazz)
    {
        File dir;
        URL classURL = clazz.getResource( "/" + clazz.getName().replace( '.', '/' ) + ".class" );
        String classFile = classURL.getFile();

        if( classFile.indexOf('!') != -1 ) {
            // Sometimes (at least on windows) we get file:F:\bla. Convert to file:/F:/bla
            if( classFile.charAt(5) != '/' ) {
                classFile = "file:/" + classFile.substring(5);
            }
            classFile = classFile.replace( '\\','/' );
            String uriSpec = classFile.substring(0, classFile.indexOf('!'));
            try {
                dir = new File( new URI( uriSpec ) );
            } catch (URISyntaxException e) {
                System.err.println("Couldn't create URI for " + uriSpec);
                throw new IllegalStateException(e.getMessage());
            }
        } else {
            dir = new File( classFile ).getParentFile();
            StringTokenizer st = new StringTokenizer( clazz.getName(), "." );

            for( int i = 0; i < st.countTokens() - 1; i++ )
            {
                dir = dir.getParentFile();
            }
        }
        return dir;
    }
}
