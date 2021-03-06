package org.nanocontainer.nanowar.sample.nanoweb;

import java.io.Serializable;
import java.util.Random;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class NumberToGuess implements Serializable {
    private Random random;
    private int number;

    public NumberToGuess(Random random) {
        this.random = random;
        newRandom();
    }
    
    public int getNumber() {
        return number;
    }

    public void newRandom() {
        number = random.nextInt(20) + 1;
    }
}