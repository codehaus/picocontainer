package picocontainer;

public class PicoInvocationTargetDisposalException extends PicoDisposalException {
    private final Throwable cause;

    public PicoInvocationTargetDisposalException(Throwable cause) {
        if (cause == null) {
            throw new IllegalArgumentException("Cause must not be null");
        }
        this.cause = cause;
    }

    public Throwable getCause() {
        return cause;
    }

    public String getMessage() {
        return "InvocationTargetException: "
                + cause.getClass().getName()
                + " " + cause.getMessage();
    }
}
