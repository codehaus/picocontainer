package cdibook.cdicomps;

/**
 * @author Paul Hammant
 * @version $Revision$
 */
// START SNIPPET: class
public class BetterSimple {

    private final SimpleDependency simpleDependency;
    private final SecondSimpleDependency secondSimpleDependency;

    public BetterSimple(SimpleDependency simpleDependency, SecondSimpleDependency secondSimpleDependency) {
        this.simpleDependency = simpleDependency;
        this.secondSimpleDependency = secondSimpleDependency;
    }

    public BetterSimple(SimpleDependency simpleDependency) {
        this(simpleDependency, new DefaultSecondSimpleDependency());
    }

    public BetterSimple(SecondSimpleDependency secondSimpleDependency) {
        this(new DefaultSimpleDependency(), secondSimpleDependency);
    }

}
// END SNIPPET: class
