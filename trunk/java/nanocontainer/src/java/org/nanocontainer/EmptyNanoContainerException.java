package org.nanocontainer;

public class EmptyNanoContainerException extends Exception {
    public EmptyNanoContainerException() {
    }

    public String getMessage() {
        return "No components in the nano container";
    }
}
