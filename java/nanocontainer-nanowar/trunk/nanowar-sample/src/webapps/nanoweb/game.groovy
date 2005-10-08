package org.nanocontainer.nanowar.sample.nanoweb;

/**
 * Simple NanoWeb Groovy action using Constructor Injection.
 *
 * @author Aslak Helles&oslash;y
 * @version $Revision: 1.5 $
 */
// START SNIPPET: class
class Game {
    public String GUESS_NEW_HINT = "Guess a number between 1 and 20"
    public NumberToGuess numberToGuess
    
    public @property int guess = Integer.MIN_VALUE
    public @property String hint = GUESS_NEW_HINT

    public Game(NumberToGuess n) {
        numberToGuess = n
    }

    public play() {
        if (guess == Integer.MIN_VALUE || guess.equals(numberToGuess.getNumber())) {
            hint = GUESS_NEW_HINT
            numberToGuess.newRandom()
        } else {
            if(guess > numberToGuess.getNumber()) {
                hint = "Too high"
            } else {
                hint = "Too low"
            }
        }
        return "input"
    }
}
// END SNIPPET: class
