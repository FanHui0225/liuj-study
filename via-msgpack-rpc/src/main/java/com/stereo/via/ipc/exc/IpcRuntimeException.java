package com.stereo.via.ipc.exc;

/**
 * Created by stereo on 16-8-9.
 */
public class IpcRuntimeException extends RuntimeException {
    /**
     *
     */
    private static final long serialVersionUID = -6865074239242615953L;
    private Throwable rootCause;

    public IpcRuntimeException() {
    }

    public IpcRuntimeException(String message) {
        super(message);
    }

    public IpcRuntimeException(String message, Throwable rootCause) {
        super(message);

        this.rootCause = rootCause;
    }

    public IpcRuntimeException(Throwable rootCause) {
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