package org.picocontainer.testmodel;

/**
 * @author Thomas Heller
 * @version $Revision$
 */
public class DecoratedTouchable implements Touchable {
    private final Touchable delegate;

    public DecoratedTouchable(Touchable delegate) {
        this.delegate = delegate;
    }

    public void touch() {
        delegate.touch();
    }
}
