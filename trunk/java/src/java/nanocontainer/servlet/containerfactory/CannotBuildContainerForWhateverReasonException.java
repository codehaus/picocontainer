package nanocontainer.servlet.containerfactory;

public class CannotBuildContainerForWhateverReasonException extends RuntimeException {
    public CannotBuildContainerForWhateverReasonException(String message, Throwable cause) {
        super(message, cause);
    }
}
