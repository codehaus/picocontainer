package cdibook.ioc;

/**
 * @author Paul Hammant
 * @version $Revision$
 */
public interface DependencyManager {
    Object lookup(String key);
}
