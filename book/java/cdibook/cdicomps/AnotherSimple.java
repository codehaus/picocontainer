package cdibook.cdicomps;

/**
 * @author Paul Hammant
 * @version $Revision$
 */
// START SNIPPET: class
public class AnotherSimple {

    private final SimpleDependency simpleDependency;
    private final SecondSimpleDependency secondSimpleDependency;

    public AnotherSimple(SimpleDependency simpleDependency, SecondSimpleDependency secondSimpleDependency) {
        this.simpleDependency = simpleDependency;
        this.secondSimpleDependency = secondSimpleDependency;
    }

    public AnotherSimple(SimpleDependency simpleDependency) {
        this.simpleDependency = simpleDependency;
        this.secondSimpleDependency = new DefaultSecondSimpleDependency();
    }

    public AnotherSimple(SecondSimpleDependency secondSimpleDependency) {
        this.secondSimpleDependency = secondSimpleDependency;
        this.simpleDependency = new DefaultSimpleDependency();
    }

}
// END SNIPPET: class
