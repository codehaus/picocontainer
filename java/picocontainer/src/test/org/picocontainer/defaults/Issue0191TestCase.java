package org.picocontainer.defaults;

import junit.framework.TestCase;
import org.picocontainer.MutablePicoContainer;

public class Issue0191TestCase extends TestCase {

    static int sharkCount = 0 ;
    static int codCount = 0 ;

    public void testFoo() {

    }

    /*
      This bug as descripbed in the bug report, cannot be reproduced. Needs work.
    */
    public void testTheBug()
    {
        MutablePicoContainer pico = new DefaultPicoContainer( ) ;
        pico.registerComponentImplementation(Shark.class);
        pico.registerComponentImplementation(Cod.class);
        try {
            pico.registerComponentImplementation(Bowl.class);
            fail("Should have barfed here with UnsatisfiableDependenciesException");
            Bowl bowl = (Bowl) pico.getComponentInstance(Bowl.class);
            Fish[] fishes = bowl.getFishes( ) ;
            for( int i = 0 ; i < fishes.length ; i++ )
                System.out.println( "fish["+i+"]="+fishes[i] ) ;
        } catch (UnsatisfiableDependenciesException e) {
            // expected, well except that there is supposed to be a different bug here.
        }
    }


    class Bowl
    {
        private final Fish[] fishes;
        private final Cod[] cods;
        public Bowl(Fish[] fishes, Cod[] cods)
        {
            this.fishes = fishes;
            this.cods = cods;
        }
        public Fish[] getFishes()
        {
            return fishes;
        }
        public Cod[] getCods()
        {
            return cods;
        }

    }

    public interface Fish
    {
    }

    class Cod implements Fish
    {
        int instanceNum ;
        public Cod( ) { instanceNum = codCount++ ; } ;
        public String toString( ) {
            return "Cod #" + instanceNum ;
        }
    }

    class Shark implements Fish
    {
        int instanceNum ;
        public Shark( ) { instanceNum = sharkCount++ ; } ;
        public String toString( ) {
            return "Shark #" + instanceNum ;
        }
    }

}
