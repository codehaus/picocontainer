/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Johan Hoogenboezem (thanks Johan)                        *
 *****************************************************************************/
package org.picocontainer.injectors;

import static org.junit.Assert.*;

import org.junit.*;
import org.picocontainer.*;
import org.picocontainer.behaviors.*;

import java.lang.reflect.Type;

public class IntoTypeTestCase {

    @Test
    public void testThatIntoSetupCorrectlyForNestedInjectionViaAFactory() throws Exception {
        MutablePicoContainer pico = new DefaultPicoContainer(new Caching());
        pico.addAdapter(new AliceFactory());
        pico.addComponent(Bob.class);
        System.out.println("Going to ask pico for a Bob");
        assertTrue(Bob.class.isAssignableFrom(Bob.class));
        Bob bob = pico.getComponent(Bob.class);
        assertNotNull(bob);
        assertNotNull(bob.getAlice());
    }


    public static interface Alice {
    }


    public static class AliceImpl implements Alice {
    }

    public static class Bob {

        private Alice alice;

        public Bob(Alice alice) {
            System.out.println("Bob gets an Alice: " + alice);
            this.alice = alice;
        }

        public Alice getAlice() {
            return alice;
        }

    }


    public static class AliceFactory extends FactoryInjector<Alice> {
        @Override
        public Alice getComponentInstance(PicoContainer container, Type into) {
            // System.out.println("Manufacturing an Alice for " + ((InjectInto) into).getIntoClass());
            if (Bob.class.isAssignableFrom(((InjectInto) into).getIntoClass())) {
                return new AliceImpl();
            } else {
                fail("Expected a " + Bob.class + ", but got a " + into + " instead.");
                return null;
            }
        }

    }


}
