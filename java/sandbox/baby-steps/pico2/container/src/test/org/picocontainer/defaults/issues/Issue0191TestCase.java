package org.picocontainer.defaults.issues;

import junit.framework.TestCase;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.UnsatisfiableDependenciesException;

public class Issue0191TestCase extends TestCase {

    static int sharkCount = 0 ;
    static int codCount = 0 ;

    /*
      This bug as descripbed in the bug report, cannot be reproduced. Needs work.
    */
    public void testTheBug()
    {
        MutablePicoContainer pico = new DefaultPicoContainer( ) ;
        pico.component(Shark.class);
        pico.component(Cod.class);
        try {
            pico.component(Bowl.class);
            Bowl bowl = (Bowl) pico.getComponent(Bowl.class);
            fail("Should have barfed here with UnsatisfiableDependenciesException");
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
