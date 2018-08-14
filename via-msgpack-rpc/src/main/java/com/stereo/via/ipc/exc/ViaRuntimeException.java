package com.stereo.via.ipc.exc;

/**
 * Created by stereo on 16-8-9.
 */
public class ViaRuntimeException extends RuntimeException {
    /**
     *
     */
    private static final long serialVersionUID = -6865074239242615953L;
    private Throwable rootCause;

    public ViaRuntimeException() {
    }

    public ViaRuntimeException(String message) {
        super(message);
    }

    public ViaRuntimeException(String message, Throwable rootCause) {
        super(message);

        this.rootCause = rootCause;
    }

    public ViaRuntimeException(Throwable rootCause) {
        super(String.valueOf(rootCause));
        this.rootCause = rootCause;
    }

    public Throwable getRootCause() {
        return this.rootCause;
    }

    public Throwable getCause() {
        return getRootCause();
    }
}