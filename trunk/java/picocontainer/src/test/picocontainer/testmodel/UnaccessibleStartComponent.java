package picocontainer.testmodel;

import java.util.List;

/**
 *
 * @author Aslak Hellesoy
 * @version $Revision: 0 $
 */
public class UnaccessibleStartComponent extends Object {
    private List messages;

    public UnaccessibleStartComponent(List messages) {
        this.messages = messages;
    }

    private final void start() {
        messages.add("started");
    }
}
