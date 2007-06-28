package org.nanocontainer.sample.nanoweb;

/**
 * Simple NanoWeb Groovy action using Constructor Injection.
 *
 * @author Aslak Helles&oslash;y
 * @version $Revision: 1.5 $
 */
// START SNIPPET: class
class Game {
    GUESS_NEW_HINT = "Guess a number between 1 and 20"

    NumberToGuess numberToGuess
    int guess = Integer.MIN_VALUE
    hint = GUESS_NEW_HINT

    Game(NumberToGuess n) {
        numberToGuess = n
    }

    play() {
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
