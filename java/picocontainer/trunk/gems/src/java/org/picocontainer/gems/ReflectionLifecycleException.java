package org.picocontainer.gems;

public class ReflectionLifecycleException extends RuntimeException {

    public ReflectionLifecycleException(String startOrStop, Throwable throwable) {
        super(startOrStop, throwable);
    }
}
