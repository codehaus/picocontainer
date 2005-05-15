package cdibook.cdicomps;

/**
 * @author Paul Hammant
 * @version $Revision$
 */
// START SNIPPET: class
public class NotSoSimple {

    private final SimpleDependency simpleDependency;
    private final SecondSimpleDependency secondSimpleDependency;

    public NotSoSimple(SimpleDependency simpleDependency, SecondSimpleDependency secondSimpleDependency) {
        this.simpleDependency = simpleDependency;
        this.secondSimpleDependency = secondSimpleDependency;
    }
}
// END SNIPPET: class
