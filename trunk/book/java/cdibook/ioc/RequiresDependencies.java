package cdibook.ioc;

/**
 * @author Paul Hammant
 * @version $Revision$
 */

public interface RequiresDependencies {
    void takeDependencies(DependencyManager dm);
}
